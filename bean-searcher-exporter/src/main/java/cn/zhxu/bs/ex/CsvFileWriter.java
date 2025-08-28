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

    private final Writer writer;

    public CsvFileWriter(OutputStream output) {
        this.writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);
    }

    @Override
    public void writeStart(List<FieldProp> columns) throws IOException {
        int size = columns.size();
        for (int col = 0; col < size; col++) {
            String text = columns.get(col).name();
            writer.write(escape(text));
            if (col < size - 1) {
                writer.write(COMMA);
            }
        }
        writer.write(CRLF);
        writer.flush();
    }

    @Override
    public void writeAndFlush(List<FieldProp> fieldProps, List<?> dataList) throws IOException {
        int size = fieldProps.size();
        for (Object data : dataList) {
            for (int col = 0; col < size; col++) {
                String text = fieldProps.get(col).withFormat(data);
                writer.write(escape(text));
                if (col < size - 1) {
                    writer.write(COMMA);
                }
            }
            writer.write(CRLF);
        }
        writer.flush();
    }

    @Override
    public void writeStop(List<FieldProp> fieldProps) { }

    @Override
    public void writeTooManyRequests() {
        throw new ExportException.TooManyRequests("Too many requests.");
    }

    protected String escape(String text) {
        if (text.contains(",")) {
            return "\"" + text.replaceAll("\"", "\"\"") + "\"";
        }
        return text;
    }

    /**
     * 关闭
     * @throws IOException IO 异常
     */
    public void close() throws IOException {
        writer.close();
    }

}
