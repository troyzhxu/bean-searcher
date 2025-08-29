package cn.zhxu.bs.ex;

import cn.zhxu.bs.util.StringUtils;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 导出字段
 * @author Troy.Zhou @ 2025-08-28
 * @since v4.5.0
 */
public class ExportField {

    private final ExprComputer computer;
    private final Field field;
    private final String exName;
    private final int exIdx;
    private final String expr;
    private final String format;

    public ExportField(ExprComputer computer, Field field, String exName, int exIdx, String expr, String format) {
        this.computer = computer;
        this.field = field;
        this.exName = exName;
        this.exIdx = exIdx;
        this.expr = expr;
        this.format = format;
    }

    /**
     * 获取该字段根据表达式转换并且格式化后的文本
     * @param obj 对象
     * @return 字段文本
     */
    public String text(Object obj) {
        Object value = compute(obj, value(obj));
        if (StringUtils.isBlank(format)) {
            return value == null ? "" : value.toString();
        }
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
    }

    /**
     * 获取字段原始值
     * @param obj 对象
     * @return 字段值
     */
    public Object value(Object obj) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new ExportException("Can not access " + field.getName(), e);
        }
    }

    /**
     * 根据表达式计算字段值
     * @param obj 对象
     * @param value 值
     * @return 计算结果
     */
    public Object compute(Object obj, Object value) {
        if (computer == null || StringUtils.isBlank(expr)) {
            return value;
        }
        try {
            return computer.compute(expr, obj, value);
        } catch (Exception e) {
            throw new IllegalStateException("Can not compute the expr [" + expr + "]", e);
        }
    }

    public ExprComputer getComputer() {
        return computer;
    }

    public Field getField() {
        return field;
    }

    public int getExIdx() {
        return exIdx;
    }

    public String getExName() {
        return exName;
    }

    public String getExpr() {
        return expr;
    }

    public String getFormat() {
        return format;
    }

}
