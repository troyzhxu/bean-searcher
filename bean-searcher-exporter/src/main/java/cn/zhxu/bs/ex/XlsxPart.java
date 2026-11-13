package cn.zhxu.bs.ex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * xlsx 文件内部各 xml 部件的输出器（只覆盖导出场景所需的最小 OOXML 子集）
 * @author Troy.Zhou @ 2026-11-12
 * @since v5.0.0
 */
class XlsxPart {

    static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

    static final String NS_MAIN = "http://schemas.openxmlformats.org/spreadsheetml/2006/main";
    static final String NS_REL = "http://schemas.openxmlformats.org/officeDocument/2006/relationships";
    static final String NS_PKG_REL = "http://schemas.openxmlformats.org/package/2006/relationships";
    static final String NS_CONTENT_TYPES = "http://schemas.openxmlformats.org/package/2006/content-types";

    private XlsxPart() { }

    /**
     * 输出 [Content_Types].xml 部件
     */
    static void writeContentTypes(ZipOutputStream zip) throws IOException {
        entry(zip, "[Content_Types].xml", XML_HEAD +
                "<Types xmlns=\"" + NS_CONTENT_TYPES + "\">" +
                "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>" +
                "<Default Extension=\"xml\" ContentType=\"application/xml\"/>" +
                "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>" +
                "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>" +
                "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>" +
                "</Types>");
    }

    /**
     * 输出包级关系部件 _rels/.rels
     */
    static void writeRootRels(ZipOutputStream zip) throws IOException {
        entry(zip, "_rels/.rels", XML_HEAD +
                "<Relationships xmlns=\"" + NS_PKG_REL + "\">" +
                "<Relationship Id=\"rId1\" Type=\"" + NS_REL + "/officeDocument\" Target=\"xl/workbook.xml\"/>" +
                "</Relationships>");
    }

    /**
     * 输出工作簿部件 xl/workbook.xml（仅一个工作表）
     */
    static void writeWorkbook(ZipOutputStream zip, String sheetName) throws IOException {
        entry(zip, "xl/workbook.xml", XML_HEAD +
                "<workbook xmlns=\"" + NS_MAIN + "\" xmlns:r=\"" + NS_REL + "\">" +
                "<sheets><sheet name=\"" + escape(sheetName) + "\" sheetId=\"1\" r:id=\"rId1\"/></sheets>" +
                "</workbook>");
    }

    /**
     * 输出工作簿关系部件 xl/_rels/workbook.xml.rels
     */
    static void writeWorkbookRels(ZipOutputStream zip) throws IOException {
        entry(zip, "xl/_rels/workbook.xml.rels", XML_HEAD +
                "<Relationships xmlns=\"" + NS_PKG_REL + "\">" +
                "<Relationship Id=\"rId1\" Type=\"" + NS_REL + "/worksheet\" Target=\"worksheets/sheet1.xml\"/>" +
                "<Relationship Id=\"rId2\" Type=\"" + NS_REL + "/styles\" Target=\"styles.xml\"/>" +
                "</Relationships>");
    }

    /**
     * 输出样式部件 xl/styles.xml<br>
     * 仅定义两个单元格格式：0 = 默认，1 = 加粗（用于表头）
     */
    static void writeStyles(ZipOutputStream zip) throws IOException {
        entry(zip, "xl/styles.xml", XML_HEAD +
                "<styleSheet xmlns=\"" + NS_MAIN + "\">" +
                "<fonts count=\"2\">" +
                "<font><sz val=\"11\"/><color theme=\"1\"/><name val=\"Calibri\"/></font>" +
                "<font><b/><sz val=\"11\"/><color theme=\"1\"/><name val=\"Calibri\"/></font>" +
                "</fonts>" +
                "<fills count=\"2\">" +
                "<fill><patternFill patternType=\"none\"/></fill>" +
                "<fill><patternFill patternType=\"gray125\"/></fill>" +
                "</fills>" +
                "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>" +
                "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>" +
                "<cellXfs count=\"2\">" +
                "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>" +
                "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\"/>" +
                "</cellXfs>" +
                "<cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles>" +
                "</styleSheet>");
    }

    /**
     * 写入一个 zip 部件
     */
    static void entry(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    /**
     * 把列索引（从 0 开始）转换为 Excel 列名，如 0 → A，25 → Z，26 → AA
     */
    static String colName(int index) {
        StringBuilder sb = new StringBuilder(2);
        int n = index + 1;
        while (n > 0) {
            int m = (n - 1) % 26;
            sb.append((char) ('A' + m));
            n = (n - 1) / 26;
        }
        return sb.reverse().toString();
    }

    /**
     * XML 转义，同时过滤 XML 1.0 不允许出现的控制字符（否则 Excel 会提示文件损坏）
     */
    static String escape(String text) {
        int len = text.length();
        StringBuilder sb = new StringBuilder(len + 16);
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            switch (c) {
                case '&': sb.append("&amp;"); break;
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&apos;"); break;
                default:
                    // XML 1.0 非法字符：0x00-0x1F（除 \t \n \r）、0xFFFE、0xFFFF，直接丢弃
                    if ((c < 0x20 && c != '\t' && c != '\n' && c != '\r') || c == 0xFFFE || c == 0xFFFF) {
                        break;
                    }
                    sb.append(c);
            }
        }
        return sb.toString();
    }

}
