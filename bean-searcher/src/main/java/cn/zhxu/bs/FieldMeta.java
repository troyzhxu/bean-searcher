package cn.zhxu.bs;

import cn.zhxu.bs.bean.Cluster;
import cn.zhxu.bs.bean.DbType;

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
     * 字段名
     * @since v4.1.0
     */
    private final String name;

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
     * 字段的聚合标志
     * @since v4.1.0
     */
    private final Cluster cluster;

    public FieldMeta(BeanMeta<?> beanMeta, String name, Field field, SqlSnippet fieldSql, String dbAlias, boolean conditional,
                     Class<? extends FieldOp>[] onlyOn, DbType dbType, Cluster cluster) {
        this.beanMeta = beanMeta;
        this.name = name;
        this.field = field;
        this.fieldSql = fieldSql;
        this.dbAlias = dbAlias;
        this.conditional = conditional;
        this.onlyOn = onlyOn;
        this.dbType = dbType;
        this.cluster = cluster;
    }

    public BeanMeta<?> getBeanMeta() {
        return beanMeta;
    }

    public Field getField() {
        return field;
    }

    public boolean selectable() {
        return field != null;
    }

    public String getName() {
        return name;
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

    public Cluster getCluster() {
        return cluster;
    }

}
