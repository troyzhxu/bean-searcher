package cn.zhxu.bs.ex;

import cn.zhxu.bs.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;

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
    private final String onlyIf;

    public ExportField(Expresser expresser, Formatter formatter,
                       Field field, String exName, int exIdx,
                       String expr, String format, String onlyIf) {
        this.expresser = expresser;
        this.formatter = formatter;
        this.field = field;
        this.exName = exName;
        this.exIdx = exIdx;
        this.expr = expr;
        this.format = format;
        this.onlyIf = onlyIf;
    }

    /**
     * 获取该字段根据表达式转换并且格式化后的文本
     * @param obj 对象
     * @return 字段文本
     */
    public String text(Object obj) {
        Object value = evaluate(obj, value(obj));
        if (value == null) return "";
        if (StringUtils.isBlank(format) || formatter == null) {
            return value.toString();
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
    public Object evaluate(Object obj, Object value) {
        if (expresser == null || StringUtils.isBlank(expr)) {
            return value;
        }
        try {
            return expresser.evaluate(expr, obj, value);
        } catch (Exception e) {
            throw new IllegalStateException("Can not compute the expr [" + expr + "]", e);
        }
    }

    public boolean onlyIf(Map<String, Object> paraMap) {
        if (expresser == null || StringUtils.isBlank(onlyIf)) {
            return true;
        }
        try {
            Object res = expresser.evaluate(onlyIf, paraMap, null);
            if (res instanceof Boolean) {
                return (Boolean) res;
            }
            if (res instanceof Number) {
                return ((Number) res).intValue() != 0;
            }
            if (res instanceof String) {
                return StringUtils.isNotBlank((String) res);
            }
            return res != null;
        } catch (Exception e) {
            throw new IllegalStateException("Can not compute the onlyIf [" + onlyIf + "]", e);
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
