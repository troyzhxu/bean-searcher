package com.ejlchina.searcher;

import java.util.List;

/**
 * 字段运算符
 * @author Troy.Zhou @ 2022-01-18
 * @since v3.3.0
 */
public interface FieldOp {

    /**
     * 该运算符的名称
     * @return 该运算符的名称
     */
    String name();

    /**
     * 猜测该运算符的名字
     * @param name 猜测名称
     * @return 是否正确
     */
    boolean isNamed(String name);

    /**
     * @return 该运算符是否不需要参数即可运算
     */
    boolean lonely();

    /**
     * 执行该运算符
     * @param sqlBuilder SQL 构建器
     * @param opPara 运算参数
     * @return 该运算符产生的 JDBC 参数列表
     */
    List<Object> operate(StringBuilder sqlBuilder, OpPara opPara);

    /**
     * 判断两个运算符是否是同一个类型
     * @param another 另一个运算符
     * @return 是否是同一个
     */
    default boolean sameTo(FieldOp another) {
        if (another == null) {
            return false;
        }
        if (another == this) {
            return true;
        }
        return isNamed(another.name());
    }

    /**
     * 运算参数
     */
    class OpPara {

        final FieldMeta fieldMeta;
        final boolean ignoreCase;
        final Object[] values;

        public OpPara(FieldMeta fieldMeta, boolean ignoreCase, Object[] values) {
            this.fieldMeta = fieldMeta;
            this.ignoreCase = ignoreCase;
            this.values = values;
        }

        public FieldMeta getFieldMeta() {
            return fieldMeta;
        }

        public String getDbField() {
            return fieldMeta.getFieldSql().getSnippet();
        }

        public boolean isIgnoreCase() {
            return ignoreCase;
        }

        public Object[] getValues() {
            return values;
        }

    }

}
