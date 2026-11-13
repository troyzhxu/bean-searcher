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
    private final int width;
    private final boolean image;

    public ExportField(Expresser expresser, Formatter formatter,
                       Field field, String exName, int exIdx,
                       String expr, String format, String onlyIf,
                       int width, boolean image) {
        this.expresser = expresser;
        this.formatter = formatter;
        this.field = field;
        this.exName = exName;
        this.exIdx = exIdx;
        this.expr = expr;
        this.format = format;
        this.onlyIf = onlyIf;
        this.width = width;
        this.image = image;
    }

    /**
     * 获取该字段根据表达式转换并且格式化后的文本
     * @param obj 对象
     * @return 字段文本
     */
    public String text(Object obj) {
        return format(evaluate(obj, value(obj)));
    }

    /**
     * @return 是否不需要格式化
     * @since v5.0.0
     */
    public boolean unformatted() {
        return StringUtils.isBlank(format) || formatter == null;
    }

    /**
     * 对字段值（已按表达式转换）进行格式化，得到最终输出的文本 <br>
     * 供需要先拿到原始值再决定输出方式的场景使用（如 Excel 导出中数字需写成数值单元格），
     * 避免重复调用 {@link #value(Object)} 与 {@link #evaluate(Object, Object)}
     * @param value 字段值（已按表达式转换）
     * @return 字段文本
     * @since v5.0.0
     */
    public String format(Object value) {
        if (value == null) {
            return "";
        }
        if (unformatted()) {
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

    /**
     * @return 列宽（仅对 Excel 导出有效），0 表示不指定列宽
     * @since v5.0.0
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return 是否图片链接字段（仅对 Excel 导出有效）
     * @since v5.0.0
     */
    public boolean isImage() {
        return image;
    }

}
