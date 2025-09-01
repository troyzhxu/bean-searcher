package cn.zhxu.bs.ex;

/**
 * 表达式计算器
 * @author Troy.Zhou @ 2025-08-28
 * @since v4.5.0
 */
public interface Expresser {

    /**
     * 表达式内使用 @ 表示当前字段的值
     */
    String VALUE_REF = "@";

    /**
     * 计算表达式
     * @param expr 表达式
     * @param obj 数据对象，可在表达式中直接用名称引用该对象中的字段值
     * @param value 字段的值，在表达式中使用 @ 符引用
     * @return 计算结果
     */
    Object evaluate(String expr, Object obj, Object value);

}
