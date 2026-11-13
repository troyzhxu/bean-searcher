package cn.zhxu.bs.ex;

import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ExcelFileWriter} 单元测试<br>
 * 所有用例都走真实的写出流程（{@code writeStart → writeAndFlush → writeStop}），
 * 再把生成的 xlsx 解压成 xml 部件做断言，因此能同时覆盖 zip 结构与单元格内容。
 *
 * @author Troy.Zhou @ 2026-11-13
 * @since v5.0.0
 */
class ExcelFileWriterTest {

    // ==================== 内容与结构 ====================

    @Test
    void testWriteExcel() throws Exception {
        Map<String, String> parts = writeXlsx(PlainOrder.class,
                new PlainOrder("A001", new BigDecimal("12.50"), 3, "含,逗号"),
                new PlainOrder("A002", new BigDecimal("100"), 7, "<b>&\"引号\"\u0001控制符"));

        // xlsx 必备部件齐全
        assertTrue(parts.keySet().containsAll(List.of(
                "[Content_Types].xml",
                "_rels/.rels",
                "xl/workbook.xml",
                "xl/_rels/workbook.xml.rels",
                "xl/styles.xml",
                "xl/worksheets/sheet1.xml")));

        String sheet = sheetOf(parts);
        // 表头：默认加粗 + inlineStr
        assertTrue(sheet.contains("<c r=\"A1\" s=\"1\" t=\"inlineStr\"><is><t>订单号</t></is></c>"));
        // 数字写成数值单元格（可在 Excel 中直接计算）
        assertTrue(sheet.contains("<c r=\"B2\"><v>12.50</v></c>"));
        assertTrue(sheet.contains("<c r=\"C3\"><v>7</v></c>"));
        // 文本单元格与 xml 转义（非法控制字符被丢弃）
        assertTrue(sheet.contains("<is><t>含,逗号</t></is>"));
        assertTrue(sheet.contains("&lt;b&gt;&amp;&quot;引号&quot;控制符"));
        // 不输出列宽定义
        assertFalse(sheet.contains("<cols>"));
    }

    @Test
    void testBatchWrite() throws Exception {
        List<PlainOrder> orders = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            orders.add(new PlainOrder("C" + i, BigDecimal.ONE, i, "备注" + i));
        }
        // 每批 3 条，最后一批只剩 1 条
        String sheet = sheetOf(writeXlsx(PlainOrder.class, orders, 3, null));

        // 表头 1 行 + 数据 10 行，行号连续
        assertEquals(11, countRows(sheet));
        assertTrue(sheet.contains("<c r=\"A11\" t=\"inlineStr\"><is><t>C9</t></is></c>"));
    }

    @Test
    void testEmptyData() throws Exception {
        // 只有表头，没有数据行
        String sheet = sheetOf(writeXlsx(PlainOrder.class, List.of(), 100, null));

        assertEquals(1, countRows(sheet));
        // 空数据时 xml 结构仍须完整闭合
        assertTrue(sheet.endsWith("</sheetData></worksheet>"));
    }

    // ==================== 样式 ====================

    @Test
    void testDefaultStyle() throws Exception {
        // 未标注 @ExcelSheet 的数据类：全部使用默认样式
        Map<String, String> parts = writeXlsx(PlainOrder.class,
                new PlainOrder("A001", BigDecimal.ONE, 1, null));

        assertTrue(workbookOf(parts).contains("name=\"" + ExcelFileWriter.DEFAULT_SHEET_NAME + "\""));
        String sheet = sheetOf(parts);
        // 默认表头加粗
        assertTrue(sheet.contains("s=\"1\""));
        // 默认不冻结首行
        assertFalse(sheet.contains("freeze"));
        // 默认使用 Excel 行高
        assertTrue(sheet.contains("<sheetFormatPr defaultRowHeight=\"15\"/>"));
    }

    @Test
    void testAnnotationApplied() throws Exception {
        // @ExcelSheet 的四个属性都应生效，且不需要任何 setter 参与
        Map<String, String> parts = writeXlsx(AnnotatedOrder.class,
                new AnnotatedOrder("A001", new BigDecimal("9.90")));

        assertTrue(workbookOf(parts).contains("name=\"订单表\""));
        String sheet = sheetOf(parts);
        // headerBold = false：表头不加粗
        assertFalse(sheet.contains("s=\"1\""));
        // freezeHeader = true：冻结首行
        assertTrue(sheet.contains("state=\"frozen\""));
        // rowHeight = 24
        assertTrue(sheet.contains("<sheetFormatPr defaultRowHeight=\"24\" customHeight=\"1\"/>"));
    }

    @Test
    void testNumericCellsOff() throws Exception {
        // 关闭数值单元格后，数字一律按文本写出
        String sheet = sheetOf(writeXlsx(PlainOrder.class,
                List.of(new PlainOrder("A001", new BigDecimal("12.50"), 3, null)), 100,
                writer -> writer.setNumericCells(false)));

        assertTrue(sheet.contains("<c r=\"B2\" t=\"inlineStr\"><is><t>12.50</t></is></c>"));
        assertTrue(sheet.contains("<c r=\"C2\" t=\"inlineStr\"><is><t>3</t></is></c>"));
        assertFalse(sheet.contains("<v>"));
    }

    @Test
    void testFormattedNumberAsText() throws Exception {
        // 配置了 format 的字段即使开着 numericCells，也按格式化后的文本写出
        String sheet = sheetOf(writeXlsx(Bonus.class, new Bonus("12500.5")));

        assertTrue(sheet.contains("<c r=\"A2\" t=\"inlineStr\"><is><t>12,500.50</t></is></c>"));
        assertFalse(sheet.contains("<v>12"));
    }

    @Test
    void testColumnWidth() throws Exception {
        String sheet = sheetOf(writeXlsx(WideOrder.class, new WideOrder("A001", "王五", "无")));

        // 只有配置了 width 的列才输出列宽，列号为 1 基；width 超过 Excel 上限（255）时截断
        assertTrue(sheet.contains("<cols>" +
                "<col min=\"2\" max=\"2\" width=\"20\" customWidth=\"1\"/>" +
                "<col min=\"3\" max=\"3\" width=\"255\" customWidth=\"1\"/>" +
                "</cols>"));
        // 未配置 width 的第 1 列不输出
        assertFalse(sheet.contains("min=\"1\""));

        // 节点顺序必须是 sheetFormatPr → cols → sheetData
        int cols = sheet.indexOf("<cols>");
        assertTrue(cols > sheet.indexOf("</sheetFormatPr>"));
        assertTrue(cols < sheet.indexOf("<sheetData>"));
    }

    @Test
    void testNoColumnWidth() throws Exception {
        // 没有任何字段配置 width 时，不应输出 cols 节点
        String sheet = sheetOf(writeXlsx(PlainOrder.class, new PlainOrder("A001", BigDecimal.ONE, 1, "x")));
        assertFalse(sheet.contains("<cols>"));
    }

    @Test
    void testImageCell() throws Exception {
        Map<String, String> parts = writeXlsx(PicUser.class,
                new PicUser("张三", "https://img.example.com/a.png?x=1&y=2"),
                new PicUser("李四", null),
                new PicUser("王五", "  "));

        String sheet = sheetOf(parts);
        // 图片链接写成 IMAGE 公式：新函数必须带 _xlfn 前缀，否则 Excel 显示 #NAME?
        // 链接中的 & 需 xml 转义
        assertTrue(sheet.contains("<c r=\"B2\"><f>_xlfn.IMAGE(&quot;https://img.example.com/a.png?x=1&amp;y=2&quot;)</f></c>"));
        // 公式单元格不写缓存值，由 calcPr 要求 Excel 打开时重算
        assertTrue(workbookOf(parts).contains("<calcPr calcId=\"0\" fullCalcOnLoad=\"1\"/>"));
        // 空链接不输出单元格
        assertFalse(sheet.contains("B3"));
        assertFalse(sheet.contains("B4"));
        // 非图片字段不受影响，仍按文本写出
        assertTrue(sheet.contains("<c r=\"A2\" t=\"inlineStr\"><is><t>张三</t></is></c>"));
    }

    // ==================== 工具方法 ====================

    @Test
    void testSheetNameSanitize() {
        // 非法字符替换为下划线
        assertEquals("a_b_c_d_e_f_g", ExcelFileWriter.sanitizeSheetName("a/b:c*d?e[f]g"));
        // 空白名回退到默认名
        assertEquals(ExcelFileWriter.DEFAULT_SHEET_NAME, ExcelFileWriter.sanitizeSheetName("  "));
        assertEquals(ExcelFileWriter.DEFAULT_SHEET_NAME, ExcelFileWriter.sanitizeSheetName(null));
        // 超长名截断到 31 个字符
        assertEquals(31, ExcelFileWriter.sanitizeSheetName("x".repeat(40)).length());
    }

    // ==================== 导出数据类 ====================

    /**
     * 无 {@link ExcelSheet} 注解：用于验证默认样式与单元格内容
     */
    static class PlainOrder {

        @Export(name = "订单号", idx = 1)
        String code;

        @Export(name = "金额", idx = 2)
        BigDecimal amount;

        @Export(name = "数量", idx = 3)
        Integer count;

        @Export(name = "备注", idx = 4)
        String remark;

        PlainOrder(String code, BigDecimal amount, Integer count, String remark) {
            this.code = code;
            this.amount = amount;
            this.count = count;
            this.remark = remark;
        }

    }

    /**
     * 全属性标注：用于验证注解对工作表名与表头样式的控制
     */
    @ExcelSheet(name = "订单表", headerBold = false, freezeHeader = true, rowHeight = 24)
    static class AnnotatedOrder {

        @Export(name = "订单号", idx = 1)
        String code;

        @Export(name = "金额", idx = 2)
        BigDecimal amount;

        AnnotatedOrder(String code, BigDecimal amount) {
            this.code = code;
            this.amount = amount;
        }

    }

    /**
     * 配置了列宽：只有 width > 0 的列才输出 &lt;cols&gt;
     */
    static class WideOrder {

        @Export(name = "订单号", idx = 1)
        String code;

        @Export(name = "姓名", idx = 2, width = 20)
        String name;

        // 超过 Excel 列宽上限，写出时应截断到 255
        @Export(name = "备注", idx = 3, width = 1000)
        String remark;

        WideOrder(String code, String name, String remark) {
            this.code = code;
            this.name = name;
            this.remark = remark;
        }

    }

    /**
     * 图片链接字段：值写成 IMAGE 公式，由 Excel 自行加载图片
     */
    static class PicUser {

        @Export(name = "姓名", idx = 1)
        String name;

        @Export(name = "头像", idx = 2, image = true, width = 12)
        String avatar;

        PicUser(String name, String avatar) {
            this.name = name;
            this.avatar = avatar;
        }

    }

    /**
     * 配置了 format 的数字字段：应写成格式化后的文本
     */
    static class Bonus {

        @Export(name = "奖金", format = "#,##0.00")
        BigDecimal bonus;

        Bonus(String bonus) {
            this.bonus = new BigDecimal(bonus);
        }

    }

    // ==================== 辅助方法 ====================

    /**
     * 走完整的写出流程，返回解压后的 xlsx 部件
     * @param beanClass 导出数据类
     * @param rows 待写出的数据
     * @return 部件名到 xml 内容的映射
     */
    static Map<String, String> writeXlsx(Class<?> beanClass, Object... rows) throws Exception {
        return writeXlsx(beanClass, List.of(rows), Integer.MAX_VALUE, null);
    }

    /**
     * 按指定批大小分批写出，返回解压后的 xlsx 部件
     * @param beanClass 导出数据类
     * @param rows 待写出的数据
     * @param batchSize 每批条数
     * @param config 写出前的写入器配置（可为 null）
     * @return 部件名到 xml 内容的映射
     */
    static Map<String, String> writeXlsx(Class<?> beanClass, List<?> rows, int batchSize,
                                       Consumer<ExcelFileWriter> config) throws Exception {
        List<ExportField> fields = new DefaultExportFieldResolver().resolve(beanClass);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExcelFileWriter writer = new ExcelFileWriter(out);
        if (config != null) {
            config.accept(writer);
        }
        writer.writeStart(beanClass, fields);
        for (int i = 0; i < rows.size(); i += batchSize) {
            writer.writeAndFlush(fields, rows.subList(i, Math.min(i + batchSize, rows.size())));
        }
        writer.writeStop(beanClass, fields);
        return unzip(out.toByteArray());
    }

    static String sheetOf(Map<String, String> parts) {
        return parts.get("xl/worksheets/sheet1.xml");
    }

    static String workbookOf(Map<String, String> parts) {
        return parts.get("xl/workbook.xml");
    }

    static int countRows(String sheet) {
        int count = 0;
        int index = sheet.indexOf("<row ");
        while (index >= 0) {
            count++;
            index = sheet.indexOf("<row ", index + 5);
        }
        return count;
    }

    /**
     * 解压 xlsx，并对每个 xml 部件做一次格式合法性校验（格式非法时 Excel 会提示“修复”）
     */
    static Map<String, String> unzip(byte[] bytes) throws Exception {
        Map<String, String> parts = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String text = new String(readAll(zip), StandardCharsets.UTF_8);
                parts.put(entry.getName(), text);
                builder.parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
            }
        }
        return parts;
    }

    static byte[] readAll(ZipInputStream zip) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = zip.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        return out.toByteArray();
    }

}
