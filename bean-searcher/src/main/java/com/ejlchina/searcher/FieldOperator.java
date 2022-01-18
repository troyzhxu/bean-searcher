package com.ejlchina.searcher;

import com.ejlchina.searcher.param.FieldParam;

import java.util.List;

/**
 * 字段运算符
 * @since v3.3.0
 */
public interface FieldOperator {

    /**
     * 猜测该运算符的名字
     * @param name 猜测名称
     * @return 是否正确
     */
    boolean isNamed(String name);

    /**
     * 执行该运算符
     * @param sqlBuilder SQL 构建器
     * @param dbField 数据库字段名
     * @param values 字段参数值
     * @return 该运算符产生的 JDBC 参数列表
     */
    List<Object> operate(StringBuilder sqlBuilder, String dbField, Object[] values);

}
