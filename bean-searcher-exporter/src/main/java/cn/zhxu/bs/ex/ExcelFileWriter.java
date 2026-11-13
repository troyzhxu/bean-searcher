package cn.zhxu.bs.ex;

import cn.zhxu.bs.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Excel 文件写入器（基于 JDK 原生 api 直接输出 xlsx，不依赖任何第三方库）<br>
 * 采用流式写出：每批次数据写完立即 flush 到输出流，内存占用与数据总量无关 <br>
 * 工作表名称、表头加粗、冻结首行、行高均由数据类上的 {@link ExcelSheet} 注解指定（行高只作用于数据行，表头行保持默认高度），
 * 各列列宽由字段上的 {@link Export#width()} 指定（未指定则该列不输出列宽）<br>
 * 注解在每次 {@link #writeStart} 调用时解析，因此同一个写入器将来可依次写出多个 sheet；<br>
 * 未标注该注解时使用默认样式：工作表名 {@value #DEFAULT_SHEET_NAME}、表头加粗、不冻结、默认行高。<br>
 * 另提供 {@link #setNumericCells} 开关（需在 {@link #writeStart} 之前设置），
 * 控制数字是否以数值单元格写出（默认开启，可在 Excel 中直接求和排序）。
 *
 * @author Troy.Zhou @ 2026-11-12
 * @since v5.0.0
 */
public class ExcelFileWriter implements FileWriter {

    public static final String FILE_EXT = ".xlsx";
    public static final String DEFAULT_SHEET_NAME = "Sheet 1";
    /** Excel 默认行高（单位：磅），未指定行高时表格使用该值，表头行也固定使用该值 */
    public static final int DEFAULT_ROW_HEIGHT = 18;

    // Excel 单元格最大字符数，超出会被截断（否则文件会损坏）
    private static final int MAX_CELL_LENGTH = 32767;
    // Excel 允许的最大列宽（单位：字符），超出会被 Excel 判为非法值
    private static final int MAX_COL_WIDTH = 255;
    // 表头单元格的样式索引（对应 styles.xml 中的第 2 个 cellXf：加粗）
    private static final int STYLE_HEADER = 1;

    /**
     * 未标注 {@link ExcelSheet} 时的默认表现<br>
     * 除工作表名外均与注解默认值一致：注解的 name 默认为 {@code "数据"}，此处取 Excel 惯例的 {@value #DEFAULT_SHEET_NAME}
     */
    private static final ExcelSheet DEFAULT_SHEET = new ExcelSheet() {
        @Override public String name() { return DEFAULT_SHEET_NAME; }
        @Override public int rowHeight() { return 0; }
        @Override public boolean headerBold() { return true; }
        @Override public boolean freezeHeader() { return false; }
        @Override public Class<? extends Annotation> annotationType() { return ExcelSheet.class; }
    };

    private final OutputStream output;
    private final BufferedOutputStream buffer;

    // 数字是否以数值单元格写出
    private boolean numericCells = true;

    private ZipOutputStream zip;
    private int rowNum;
    // 行属性片段（行高），在 writeStart 时预拼好，避免每行重复拼装
    private String rowAttrs = "";
    private String headerAttrs = "";

    public ExcelFileWriter(OutputStream output) {
        this.output = output;
        this.buffer = new BufferedOutputStream(output, 8192);
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
        this.rowAttrs = "";
        this.headerAttrs = "";

        // 每个 sheet 的样式完全由 @ExcelSheet 决定，每次 writeStart 独立解析（便于将来一个文件写多个 sheet）
        ExcelSheet sheet = Objects.requireNonNullElse(beanClass.getAnnotation(ExcelSheet.class), DEFAULT_SHEET);

        zip = new ZipOutputStream(buffer, StandardCharsets.UTF_8);
        XlsxPart.writeContentTypes(zip);
        XlsxPart.writeRootRels(zip);
        XlsxPart.writeWorkbook(zip, sanitizeSheetName(sheet.name()));
        XlsxPart.writeWorkbookRels(zip);
        XlsxPart.writeStyles(zip);

        zip.putNextEntry(new ZipEntry("xl/worksheets/sheet1.xml"));
        write(XlsxPart.XML_HEAD);
        write("<worksheet xmlns=\"" + XlsxPart.NS_MAIN + "\">");
        if (sheet.freezeHeader()) {
            write("<sheetViews><sheetView tabSelected=\"1\" workbookViewId=\"0\">" +
                    "<pane ySplit=\"1\" topLeftCell=\"A2\" activePane=\"bottomLeft\" state=\"frozen\"/>" +
                    "<selection pane=\"bottomLeft\" activeCell=\"A2\" sqref=\"A2\"/>" +
                    "</sheetView></sheetViews>");
        }
        // 行高必须逐行写出 ht，Excel 并不认 sheetFormatPr 里的 defaultRowHeight（只影响新增行）
        int rowHeight = sheet.rowHeight();
        if (rowHeight > 0 && rowHeight != DEFAULT_ROW_HEIGHT) {
            rowAttrs = " ht=\"" + rowHeight + "\" customHeight=\"1\"";
            // 表头行固定用 Excel 默认行高
            headerAttrs = " ht=\"" + DEFAULT_ROW_HEIGHT + "\" customHeight=\"1\"";
        }
        write(rowHeight > 0
                ? "<sheetFormatPr defaultRowHeight=\"" + rowHeight + "\" customHeight=\"1\"/>"
                : "<sheetFormatPr defaultRowHeight=\"" + DEFAULT_ROW_HEIGHT + "\"/>");
        writeCols(fields);
        write("<sheetData>");
        writeHeaderRow(fields, sheet.headerBold());
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
     * 输出列宽定义，仅在至少一个字段配置了 {@link Export#width()} 时输出<br>
     * 该节点必须位于 sheetFormatPr 之后、sheetData 之前，否则不符合 OOXML 架构
     * @param fields 文件字段列表
     */
    protected void writeCols(List<ExportField> fields) throws IOException {
        StringBuilder sb = null;
        for (int i = 0; i < fields.size(); i++) {
            int width = fields.get(i).getWidth();
            if (width <= 0) {
                continue;
            }
            if (sb == null) {
                sb = new StringBuilder(32 + fields.size() * 24).append("<cols>");
            }
            int col = i + 1;
            sb.append("<col min=\"").append(col).append("\" max=\"").append(col)
                    .append("\" width=\"").append(Math.min(width, MAX_COL_WIDTH))
                    .append("\" customWidth=\"1\"/>");
        }
        if (sb != null) {
            write(sb.append("</cols>").toString());
        }
    }

    /**
     * 写出表头行<br>
     * 行高只作用于数据行，因此表头行在这里显式使用 Excel 默认行高
     * @param fields 文件字段列表
     * @param bold 表头是否加粗
     */
    protected void writeHeaderRow(List<ExportField> fields, boolean bold) throws IOException {
        StringBuilder sb = new StringBuilder(64 + fields.size() * 32);
        int row = ++rowNum;
        String style = bold ? " s=\"" + STYLE_HEADER + "\"" : "";
        sb.append("<row r=\"").append(row).append('"').append(headerAttrs).append('>');
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
        sb.append("<row r=\"").append(row).append('"').append(rowAttrs).append('>');
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
        // 图片字段：值即网络图片链接，交给 Excel 的 IMAGE 函数加载，无需下载图片
        if (field.isImage()) {
            writeImageCell(sb, ref, value);
            return;
        }
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
     * 写出图片单元格：字段值是网络图片链接，用 IMAGE 公式让 Excel 自行加载图片<br>
     * IMAGE 是 Excel 2022 之后新增的函数，在 xlsx 的 xml 中必须写成 {@code _xlfn.IMAGE}，
     * 否则 Excel 会识别为 #NAME? 错误；另外公式单元格不写缓存值，由 {@code calcPr} 要求 Excel 打开时重算
     * @param sb 行 xml 缓冲
     * @param ref 单元格引用
     * @param value 图片链接
     */
    protected void writeImageCell(StringBuilder sb, String ref, Object value) {
        String url = value.toString().trim();
        if (url.isEmpty()) {
            return;
        }
        // 链接中的双引号按 Excel 公式规则转义为两个双引号，之后再由 xml 转义
        url = XlsxPart.escape(url.replace("\"", "\"\""));
        sb.append("<c r=\"").append(ref).append("\"><f>_xlfn.IMAGE(&quot;")
                .append(url).append("&quot;)</f></c>");
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

    public OutputStream getOutput() {
        return output;
    }

    /**
     * 规范化工作表名称：非法字符替换为下划线，超长截断到 31 个字符
     */
    static String sanitizeSheetName(String sheetName) {
        if (StringUtils.isBlank(sheetName)) {
            return DEFAULT_SHEET_NAME;
        }
        String name = sheetName.replaceAll("[:\\\\/?*\\[\\]]", "_");
        return name.length() > 31 ? name.substring(0, 31) : name;
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
