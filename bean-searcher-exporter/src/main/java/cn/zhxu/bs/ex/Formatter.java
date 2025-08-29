package cn.zhxu.bs.ex;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 格式化器
 * @author Troy.Zhou @ 2025-08-29
 * @since v4.5.0
 */
public interface Formatter {

    /**
     * 默认格式化器
     */
    Formatter DEFAULT = (format, value) -> {
        if (value instanceof Date) {
            return new SimpleDateFormat(format).format((Date) value);
        }
        if (value instanceof TemporalAccessor) {
            return DateTimeFormatter.ofPattern(format).format((TemporalAccessor) value);
        }
        if (value instanceof Number) {
            return new DecimalFormat(format).format(value);
        }
        return String.format(format, value);
    };

    /**
     * 格式化
     * @param format 格式
     * @param value 值
     * @return 格式化后的文本
     */
    String format(String format, Object value);

}
