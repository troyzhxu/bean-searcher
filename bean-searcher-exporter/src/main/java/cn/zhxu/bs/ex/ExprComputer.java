package cn.zhxu.bs.ex;

/**
 * 表达式计算器
 * @author Troy.Zhou @ 2025-08-28
 * @since v4.5.0
 */
public interface ExprComputer {

    /**
     * 计算表达式
     * @param expr 表达式
     * @param obj 对象
     * @param value 字段值
     * @return 计算结果
     */
    Object compute(String expr, Object obj, Object value);

}
