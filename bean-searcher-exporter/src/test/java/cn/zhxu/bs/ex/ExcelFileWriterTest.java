package cn.zhxu.bs.ex;

import org.junit.jupiter.api.Test;

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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

class ExcelFileWriterTest {

    static class Order {

        @Export(name = "订单号", idx = 1)
        String code;

        @Export(name = "金额", idx = 2)
        BigDecimal amount;

        @Export(name = "数量", idx = 3)
        Integer count;

        @Export(name = "备注", idx = 4)
        String remark;

        Order(String code, BigDecimal amount, Integer count, String remark) {
            this.code = code;
            this.amount = amount;
            this.count = count;
            this.remark = remark;
        }

    }

    @Test
    void testWriteExcel() throws Exception {
        List<ExportField> fields = new DefaultExportFieldResolver().resolve(Order.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExcelFileWriter writer = new ExcelFileWriter(out, "订单数据");
        writer.writeStart(Order.class, fields);
        writer.writeAndFlush(fields, List.of(
                new Order("A001", new BigDecimal("12.50"), 3, "含,逗号"),
                new Order("A002", new BigDecimal("100"), 7, "<b>&\"引号\"\u0001控制符")
        ));
        writer.writeStop(Order.class, fields);

        Map<String, String> parts = unzip(out.toByteArray());
        assertTrue(parts.containsKey("[Content_Types].xml"));
        assertTrue(parts.containsKey("_rels/.rels"));
        assertTrue(parts.containsKey("xl/workbook.xml"));
        assertTrue(parts.containsKey("xl/_rels/workbook.xml.rels"));
        assertTrue(parts.containsKey("xl/styles.xml"));
        assertTrue(parts.containsKey("xl/worksheets/sheet1.xml"));

        String sheet = parts.get("xl/worksheets/sheet1.xml");
        // 冻结首行
        assertTrue(sheet.contains("<pane ySplit=\"1\" topLeftCell=\"A2\" activePane=\"bottomLeft\" state=\"frozen\"/>"));
        // 表头：加粗样式 + inlineStr
        assertTrue(sheet.contains("<c r=\"A1\" s=\"1\" t=\"inlineStr\"><is><t>订单号</t></is></c>"));
        // 数字写成数值单元格（可在 Excel 中直接计算）
        assertTrue(sheet.contains("<c r=\"B2\"><v>12.50</v></c>"));
        assertTrue(sheet.contains("<c r=\"C3\"><v>7</v></c>"));
        // 文本单元格与 xml 转义（非法控制字符被丢弃）
        assertTrue(sheet.contains("<is><t>含,逗号</t></is>"));
        assertTrue(sheet.contains("&lt;b&gt;&amp;&quot;引号&quot;控制符"));
        // 不输出列宽定义
        assertFalse(sheet.contains("<cols>"));
        // 工作表名称
        assertTrue(parts.get("xl/workbook.xml").contains("name=\"订单数据\""));
    }

    @Test
    void testBatchWrite() throws Exception {
        List<ExportField> fields = new DefaultExportFieldResolver().resolve(Order.class);
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            orders.add(new Order("C" + i, BigDecimal.ONE, i, "备注" + i));
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExcelFileWriter writer = new ExcelFileWriter(out);
        writer.writeStart(Order.class, fields);
        for (int i = 0; i < orders.size(); i += 3) {
            writer.writeAndFlush(fields, orders.subList(i, Math.min(i + 3, orders.size())));
        }
        writer.writeStop(Order.class, fields);

        Map<String, String> parts = unzip(out.toByteArray());
        String sheet = parts.get("xl/worksheets/sheet1.xml");
        assertEquals(11, countRows(sheet));
        assertTrue(sheet.contains("<c r=\"A11\" t=\"inlineStr\"><is><t>C9</t></is></c>"));
        // 默认工作表名
        assertTrue(parts.get("xl/workbook.xml").contains("name=\"Sheet 1\""));
    }

    @Test
    void testEmptyData() throws Exception {
        List<ExportField> fields = new DefaultExportFieldResolver().resolve(Order.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExcelFileWriter writer = new ExcelFileWriter(out);
        writer.writeStart(Order.class, fields);
        writer.writeAndFlush(fields, List.of());
        writer.writeStop(Order.class, fields);

        String sheet = unzip(out.toByteArray()).get("xl/worksheets/sheet1.xml");
        // 只有表头一行，且 xml 结构完整
        assertEquals(1, countRows(sheet));
        assertTrue(sheet.endsWith("</sheetData></worksheet>"));
    }

    @Test
    void testSheetNameSanitize() {
        ExcelFileWriter writer = new ExcelFileWriter(new ByteArrayOutputStream(), "a/b:c*d?e[f]g");
        assertEquals("a_b_c_d_e_f_g", writer.getSheetName());
        writer.setSheetName("  ");
        assertEquals("Sheet 1", writer.getSheetName());
        writer.setSheetName("x".repeat(40));
        assertEquals(31, writer.getSheetName().length());
    }

    @Test
    void testSwitchOff() throws Exception {
        List<ExportField> fields = new DefaultExportFieldResolver().resolve(Order.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExcelFileWriter writer = new ExcelFileWriter(out);
        writer.setHeaderBold(false);
        writer.setFreezeHeader(false);
        writer.setNumericCells(false);
        writer.writeStart(Order.class, fields);
        writer.writeAndFlush(fields, List.of(new Order("A001", new BigDecimal("12.50"), 3, "x")));
        writer.writeStop(Order.class, fields);

        String sheet = unzip(out.toByteArray()).get("xl/worksheets/sheet1.xml");
        // 表头不加粗
        assertTrue(sheet.contains("<c r=\"A1\" t=\"inlineStr\"><is><t>订单号</t></is></c>"));
        assertFalse(sheet.contains("s=\"1\""));
        // 不冻结首行
        assertFalse(sheet.contains("freeze"));
        assertFalse(sheet.contains("<sheetViews>"));
        // 数字按文本写出
        assertTrue(sheet.contains("<c r=\"B2\" t=\"inlineStr\"><is><t>12.50</t></is></c>"));
        assertTrue(sheet.contains("<c r=\"C2\" t=\"inlineStr\"><is><t>3</t></is></c>"));
        assertFalse(sheet.contains("<v>"));
    }

    @Test
    void testSwitchOn() throws Exception {
        List<ExportField> fields = new DefaultExportFieldResolver().resolve(Order.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExcelFileWriter writer = new ExcelFileWriter(out);
        assertTrue(writer.isHeaderBold());
        assertTrue(writer.isFreezeHeader());
        assertTrue(writer.isNumericCells());
        writer.writeStart(Order.class, fields);
        writer.writeAndFlush(fields, List.of(new Order("A001", new BigDecimal("12.50"), 3, "x")));
        writer.writeStop(Order.class, fields);

        String sheet = unzip(out.toByteArray()).get("xl/worksheets/sheet1.xml");
        assertTrue(sheet.contains("<c r=\"A1\" s=\"1\" t=\"inlineStr\">"));
        assertTrue(sheet.contains("state=\"frozen\""));
        assertTrue(sheet.contains("<c r=\"C2\"><v>3</v></c>"));
    }

    static class Bonus {

        @Export(name = "奖金", format = "#,##0.00")
        BigDecimal bonus;

        Bonus(String bonus) {
            this.bonus = new BigDecimal(bonus);
        }

    }

    @Test
    void testFormattedNumberAsText() throws Exception {
        List<ExportField> fields = new DefaultExportFieldResolver().resolve(Bonus.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExcelFileWriter writer = new ExcelFileWriter(out);
        writer.writeStart(Bonus.class, fields);
        writer.writeAndFlush(fields, List.of(new Bonus("12500.5")));
        writer.writeStop(Bonus.class, fields);

        String sheet = unzip(out.toByteArray()).get("xl/worksheets/sheet1.xml");
        // 配置了 format 的数字按格式化后的文本写出，而不是数值单元格
        assertTrue(sheet.contains("<c r=\"A2\" t=\"inlineStr\"><is><t>12,500.50</t></is></c>"));
        assertFalse(sheet.contains("<v>12"));
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
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String text = new String(readAll(zip), StandardCharsets.UTF_8);
                parts.put(entry.getName(), text);
                factory.newDocumentBuilder().parse(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
            }
        }
        return parts;
    }

    static byte[] readAll(ZipInputStream zip) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = zip.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        return out.toByteArray();
    }

}
