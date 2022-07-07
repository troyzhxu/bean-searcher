package com.ejlchina.searcher;

import com.ejlchina.searcher.bean.DbType;

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
    private final Class<? extends FieldOp>[] onlyOn;

    /**
     * 数据库中该字段的类型，用于转换用户传入的检索参数值，为 {@link DbType#UNKNOWN } 时表示不需要转换
     * @since v3.8.0
     */
    private final DbType dbType;

    /**
     * 专门为该字段绑定的 FieldConvertor，为空则会从公用 convertors 匹配查找
     * @since v3.8.1
     */
    private final Class<? extends Convertor> convClazz;

    public FieldMeta(BeanMeta<?> beanMeta, Field field, SqlSnippet fieldSql, String dbAlias, boolean conditional,
                     Class<? extends FieldOp>[] onlyOn, DbType dbType, Class<? extends Convertor> convClazz) {
        this.beanMeta = beanMeta;
        this.field = field;
        this.fieldSql = fieldSql;
        this.dbAlias = dbAlias;
        this.conditional = conditional;
        this.onlyOn = onlyOn;
        this.dbType = dbType;
        this.convClazz = convClazz;
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

    public Class<? extends FieldOp>[] getOnlyOn() {
        return onlyOn;
    }

    public DbType getDbType() {
        return dbType;
    }

    public Class<? extends Convertor> getConvClazz() {
        return convClazz;
    }

}
