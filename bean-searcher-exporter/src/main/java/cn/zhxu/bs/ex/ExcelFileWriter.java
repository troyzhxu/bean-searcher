package cn.zhxu.bs.ex;

import cn.zhxu.bs.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Excel 文件写入器（基于 JDK 原生 api 直接输出 xlsx，不依赖任何第三方库）<br>
 * 采用流式写出：每批次数据写完立即 flush 到输出流，内存占用与数据总量无关 <br>
 * 以下三个特性均可通过属性开关控制（均默认开启，需在 {@link #writeStart} 之前设置）：
 * <ul>
 *     <li>{@link #setHeaderBold}：表头加粗</li>
 *     <li>{@link #setFreezeHeader}：冻结首行</li>
 *     <li>{@link #setNumericCells}：数字以数值单元格写出（可在 Excel 中直接求和排序）</li>
 * </ul>
 *
 * @author Troy.Zhou @ 2026-11-12
 * @since v5.0.0
 */
public class ExcelFileWriter implements FileWriter {

    public static final String FILE_EXT = ".xlsx";
    public static final String DEFAULT_SHEET_NAME = "Sheet 1";

    // Excel 单元格最大字符数，超出会被截断（否则文件会损坏）
    private static final int MAX_CELL_LENGTH = 32767;
    // 表头单元格的样式索引（对应 styles.xml 中的第 2 个 cellXf：加粗）
    private static final int STYLE_HEADER = 1;

    private final OutputStream output;
    private final BufferedOutputStream buffer;

    private String sheetName = DEFAULT_SHEET_NAME;
    // 表头是否加粗
    private boolean headerBold = true;
    // 是否冻结首行
    private boolean freezeHeader = true;
    // 数字是否以数值单元格写出
    private boolean numericCells = true;

    private ZipOutputStream zip;
    private int rowNum;

    public ExcelFileWriter(OutputStream output) {
        this.output = output;
        this.buffer = new BufferedOutputStream(output, 8192);
    }

    public ExcelFileWriter(OutputStream output, String sheetName) {
        this(output);
        setSheetName(sheetName);
    }

    /**
     * 添加文件扩展名
     * @param filename 文件名
     * @return 文件名
     */
    public static String withFileExt(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("filename can not be null.");
        }
        return filename.endsWith(FILE_EXT) ? filename : filename + FILE_EXT;
    }

    @Override
    public void writeStart(Class<?> beanClass, List<ExportField> fields) throws IOException {
        if (zip != null) {
            throw new ExportException("The writeStart method can only be called once.");
        }
        if (fields == null || fields.isEmpty()) {
            throw new ExportException("The export fields can not be empty.");
        }
        this.rowNum = 0;

        zip = new ZipOutputStream(buffer, StandardCharsets.UTF_8);
        XlsxPart.writeContentTypes(zip);
        XlsxPart.writeRootRels(zip);
        XlsxPart.writeWorkbook(zip, sheetName);
        XlsxPart.writeWorkbookRels(zip);
        XlsxPart.writeStyles(zip);

        zip.putNextEntry(new ZipEntry("xl/worksheets/sheet1.xml"));
        write(XlsxPart.XML_HEAD);
        write("<worksheet xmlns=\"" + XlsxPart.NS_MAIN + "\">");
        if (freezeHeader) {
            write("<sheetViews><sheetView tabSelected=\"1\" workbookViewId=\"0\">" +
                    "<pane ySplit=\"1\" topLeftCell=\"A2\" activePane=\"bottomLeft\" state=\"frozen\"/>" +
                    "<selection pane=\"bottomLeft\" activeCell=\"A2\" sqref=\"A2\"/>" +
                    "</sheetView></sheetViews>");
        }
        write("<sheetFormatPr defaultRowHeight=\"15\"/>");
        write("<sheetData>");
        writeHeaderRow(fields);
        zip.flush();
        buffer.flush();
    }

    @Override
    public void writeAndFlush(List<ExportField> fields, List<?> dataList) throws IOException {
        if (zip == null) {
            throw new ExportException("You must call writeStart before writeAndFlush.");
        }
        for (Object data : dataList) {
            writeDataRow(fields, data);
        }
        zip.flush();
        buffer.flush();
    }

    @Override
    public void writeStop(Class<?> beanClass, List<ExportField> fields) throws IOException {
        if (zip == null) {
            return;
        }
        write("</sheetData></worksheet>");
        zip.closeEntry();
        zip.finish();
        zip.flush();
        buffer.flush();
        zip = null;
    }

    @Override
    public void onTooManyRequests() throws IOException {
        throw new ExportException.TooManyRequests("Too many requests.");
    }

    /**
     * 写出表头行
     * @param fields 文件字段列表
     */
    protected void writeHeaderRow(List<ExportField> fields) throws IOException {
        StringBuilder sb = new StringBuilder(64 + fields.size() * 32);
        int row = ++rowNum;
        String style = headerBold ? " s=\"" + STYLE_HEADER + "\"" : "";
        sb.append("<row r=\"").append(row).append("\">");
        for (int col = 0; col < fields.size(); col++) {
            String name = fields.get(col).getExName();
            String text = name == null ? "" : name;
            sb.append("<c r=\"").append(XlsxPart.colName(col)).append(row).append('"')
                    .append(style).append(" t=\"inlineStr\">")
                    .append(textXml(text)).append("</c>");
        }
        sb.append("</row>");
        write(sb.toString());
    }

    /**
     * 写出一条数据行
     * @param fields 文件字段列表
     * @param data 数据对象
     */
    protected void writeDataRow(List<ExportField> fields, Object data) throws IOException {
        StringBuilder sb = new StringBuilder(64 + fields.size() * 24);
        int row = ++rowNum;
        sb.append("<row r=\"").append(row).append("\">");
        for (int col = 0; col < fields.size(); col++) {
            writeCell(sb, col, row, fields.get(col), data);
        }
        sb.append("</row>");
        write(sb.toString());
    }

    /**
     * 写出一个单元格
     * @param sb 行 xml 缓冲
     * @param col 列索引（从 0 开始）
     * @param row 行号（从 1 开始）
     * @param field 导出字段
     * @param data 数据对象
     */
    protected void writeCell(StringBuilder sb, int col, int row, ExportField field, Object data) {
        Object value = field.evaluate(data, field.value(data));
        if (value == null) {
            return;
        }
        String ref = XlsxPart.colName(col) + row;
        // 只有未配置格式化的字段才写成数值 / 布尔单元格，否则一律按格式化后的文本写出
        if (field.unformatted()) {
            if (numericCells && value instanceof Number number) {
                String text = numberText(number);
                if (text != null) {
                    // 数值单元格：Excel 可直接参与计算
                    sb.append("<c r=\"").append(ref).append("\"><v>").append(text).append("</v></c>");
                    return;
                }
            }
            if (value instanceof Boolean bool) {
                sb.append("<c r=\"").append(ref).append("\" t=\"b\"><v>")
                        .append(bool ? '1' : '0').append("</v></c>");
                return;
            }
        }
        String text = field.format(value);
        if (text == null) {
            return;
        }
        if (text.length() > MAX_CELL_LENGTH) {
            text = text.substring(0, MAX_CELL_LENGTH);
        }
        sb.append("<c r=\"").append(ref).append("\" t=\"inlineStr\">")
                .append(textXml(text)).append("</c>");
    }

    /**
     * 数值转 xlsx 数值文本，无法表示（NaN / Infinity）时返回 null
     */
    private String numberText(Number number) {
        if (number instanceof BigDecimal decimal) {
            return decimal.toPlainString();
        }
        if (number instanceof Double || number instanceof Float) {
            double d = number.doubleValue();
            return Double.isFinite(d) ? number.toString() : null;
        }
        return number.toString();
    }

    /**
     * 生成 inlineStr 的文本节点
     */
    private String textXml(String text) {
        String escaped = XlsxPart.escape(text);
        boolean preserve = !text.isBlank() && !text.strip().equals(text);
        return preserve
                ? "<is><t xml:space=\"preserve\">" + escaped + "</t></is>"
                : "<is><t>" + escaped + "</t></is>";
    }

    private void write(String xml) throws IOException {
        zip.write(xml.getBytes(StandardCharsets.UTF_8));
    }

    public String getSheetName() {
        return sheetName;
    }

    /**
     * 设置工作表名称（非法字符会被替换，超长会被截断）
     * @param sheetName 工作表名称
     */
    public void setSheetName(String sheetName) {
        if (StringUtils.isBlank(sheetName)) {
            this.sheetName = DEFAULT_SHEET_NAME;
            return;
        }
        String name = sheetName.replaceAll("[:\\\\/?*\\[\\]]", "_");
        if (name.length() > 31) {
            name = name.substring(0, 31);
        }
        this.sheetName = name;
    }

    public OutputStream getOutput() {
        return output;
    }

    public boolean isHeaderBold() {
        return headerBold;
    }

    /**
     * 设置表头是否加粗（默认 true），需在 writeStart 之前调用
     * @param headerBold 表头是否加粗
     */
    public void setHeaderBold(boolean headerBold) {
        this.headerBold = headerBold;
    }

    public boolean isFreezeHeader() {
        return freezeHeader;
    }

    /**
     * 设置是否冻结首行（默认 true），需在 writeStart 之前调用
     * @param freezeHeader 是否冻结首行
     */
    public void setFreezeHeader(boolean freezeHeader) {
        this.freezeHeader = freezeHeader;
    }

    public boolean isNumericCells() {
        return numericCells;
    }

    /**
     * 设置数字是否以数值单元格写出（默认 true）<br>
     * 开启时数字在 Excel 中可直接求和、排序；关闭时数字按文本写出，需在 writeStart 之前调用
     * @param numericCells 数字是否以数值单元格写出
     */
    public void setNumericCells(boolean numericCells) {
        this.numericCells = numericCells;
    }

}
