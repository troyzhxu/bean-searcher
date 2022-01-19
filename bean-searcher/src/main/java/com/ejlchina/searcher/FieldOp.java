package com.ejlchina.searcher;

import com.ejlchina.searcher.dialect.Dialect;

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
     * 执行该运算符
     * @param sqlBuilder SQL 构建器
     * @param opPara 运算参数
     * @param dialect 方言
     * @return 该运算符产生的 JDBC 参数列表
     */
    List<Object> operate(StringBuilder sqlBuilder, OpPara opPara, Dialect dialect);

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

    class OpPara {

        final String dbField;
        final boolean ignoreCase;
        final Object[] values;

        public OpPara(String dbField, boolean ignoreCase, Object[] values) {
            this.dbField = dbField;
            this.ignoreCase = ignoreCase;
            this.values = values;
        }

        public String getDbField() {
            return dbField;
        }

        public boolean isIgnoreCase() {
            return ignoreCase;
        }

        public Object[] getValues() {
            return values;
        }

    }

}
