package cn.zhxu.bs.ex;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * CSV 文件写入器
 * @author Troy.Zhou @ 2025-08-28
 * @since v4.5.0
 */
public class CsvFileWriter implements FileWriter {

    public static final String COMMA = ",";
    public static final String  CRLF = "\r\n";
    public static final String FILE_EXT = ".csv";

    private final Writer writer;

    public CsvFileWriter(Writer writer) {
        this.writer = writer;
    }

    public CsvFileWriter(OutputStream output) {
        this.writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);
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
    public void writeStart(List<ExportField> fields) throws IOException {
        int size = fields.size();
        for (int col = 0; col < size; col++) {
            String text = fields.get(col).name();
            writer.write(escape(text));
            if (col < size - 1) {
                writer.write(COMMA);
            }
        }
        writer.write(CRLF);
        writer.flush();
    }

    @Override
    public void writeAndFlush(List<ExportField> fields, List<?> dataList) throws IOException {
        int size = fields.size();
        for (Object data : dataList) {
            for (int col = 0; col < size; col++) {
                String text = fields.get(col).withFormat(data);
                writer.write(escape(text));
                if (col < size - 1) {
                    writer.write(COMMA);
                }
            }
            writer.write(CRLF);
        }
        writer.flush();
    }

    /**
     * 追加内容
     * @param content 内容
     * @throws IOException IO 异常
     */
    public void writeAndFlush(String content) throws IOException {
        if (content != null) {
            writer.write(content);
        }
        writer.flush();
    }

    @Override
    public void writeStop(List<ExportField> fields) { }

    @Override
    public void writeTooManyRequests() throws IOException {
        throw new ExportException.TooManyRequests("Too many requests.");
    }

    protected String escape(String text) {
        if (text.contains(",")) {
            return "\"" + text.replaceAll("\"", "\"\"") + "\"";
        }
        return text;
    }

    public Writer getWriter() {
        return writer;
    }

}
