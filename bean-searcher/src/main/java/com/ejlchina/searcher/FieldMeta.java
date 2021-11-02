package com.ejlchina.searcher;

import com.ejlchina.searcher.param.Operator;

import java.lang.reflect.Method;

/**
 * 字段元信息
 */
public class FieldMeta {

    /**
     * Java 字段名
     */
    private final String name;

    /**
     * 该字段的类型
     */
    private final Class<?> type;

    /**
     * 该字段的 Set 方法
     */
    private final Method setter;

    /**
     * 该字段对应的 SQL 片段
     */
    private final SqlSnippet fieldSql;

    /**
     * 该字段对应的 DB 字段别名
     */
    private final String dbAlias;

    /**
     * 该字段是否可作为检索参数
     */
    private final boolean conditional;

    /**
     * 该字段可作为检索时，被允许的运算符
     */
    private final Operator[] onlyOn;


    public FieldMeta(String name, Class<?> type, Method setter, SqlSnippet fieldSql, String dbAlias, boolean conditional, Operator[] onlyOn) {
        this.name = name;
        this.type = type;
        this.setter = setter;
        this.fieldSql = fieldSql;
        this.dbAlias = dbAlias;
        this.conditional = conditional;
        this.onlyOn = onlyOn;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public Method getSetter() {
        return setter;
    }

    public SqlSnippet getFieldSql() {
        return fieldSql;
    }

    public String getDbAlias() {
        return dbAlias;
    }

    public boolean isConditional() {
        return conditional;
    }

    public Operator[] getOnlyOn() {
        return onlyOn;
    }

}
