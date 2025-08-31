package cn.zhxu.bs.ex;

import cn.zhxu.bs.util.StringUtils;

import java.lang.reflect.Field;

/**
 * 导出字段
 * @author Troy.Zhou @ 2025-08-28
 * @since v4.5.0
 */
public class ExportField {

    private final Expresser expresser;
    private final Formatter formatter;
    private final Field field;
    private final String exName;
    private final int exIdx;
    private final String expr;
    private final String format;

    public ExportField(Expresser expresser, Formatter formatter, Field field,
                       String exName, int exIdx,
                       String expr, String format) {
        this.expresser = expresser;
        this.formatter = formatter;
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
        if (StringUtils.isBlank(format) || formatter == null) {
            return value == null ? "" : value.toString();
        }
        return formatter.format(format, value);
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
        if (expresser == null || StringUtils.isBlank(expr)) {
            return value;
        }
        try {
            return expresser.compute(expr, obj, value);
        } catch (Exception e) {
            throw new IllegalStateException("Can not compute the expr [" + expr + "]", e);
        }
    }

    public Expresser getExpresser() {
        return expresser;
    }

    public Formatter getFormatter() {
        return formatter;
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
