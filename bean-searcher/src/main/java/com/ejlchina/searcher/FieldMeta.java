package com.ejlchina.searcher;

import java.lang.reflect.Field;

/**
 * 字段元信息
 */
public class FieldMeta {

    /**
     * 所属的 Bean 元信息
     */
    private final BeanMeta<?> beanMeta;

    /**
     * Java 字段
     */
    private final Field field;

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
    private final FieldOp[] onlyOn;


    public FieldMeta(BeanMeta<?> beanMeta, Field field, SqlSnippet fieldSql,
                     String dbAlias, boolean conditional, FieldOp[] onlyOn) {
        this.beanMeta = beanMeta;
        this.field = field;
        this.fieldSql = fieldSql;
        this.dbAlias = dbAlias;
        this.conditional = conditional;
        this.onlyOn = onlyOn;
    }

    public BeanMeta<?> getBeanMeta() {
        return beanMeta;
    }

    public Field getField() {
        return field;
    }

    public String getName() {
        return field.getName();
    }

    public Class<?> getType() {
        return field.getType();
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

    public FieldOp[] getOnlyOn() {
        return onlyOn;
    }

}
