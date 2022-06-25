package com.ejlchina.searcher;

import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.DbType;
import com.ejlchina.searcher.bean.InheritType;
import com.ejlchina.searcher.bean.SearchBean;

import java.lang.reflect.Field;

/**
 * 在 {@link SearchBean } 或 {@link DbField } 缺省时
 * 自动与数据库表名与字段名映射
 * @author Troy.Zhou @ 2021-11-01
 * @since v3.0.0
 */
public interface DbMapping {

    /**
     * SearchBean 的继承类型
     * @since v3.2.0
     * @param beanClass SearchBean 的 Class
     * @return 继承类型
     */
    InheritType inheritType(Class<?> beanClass);

    /**
     * 根据 beanClass 获取表名
     * 在 SearchBean 的类上没加 {@link SearchBean } 注解时 或 {@link SearchBean#tables()} 属性为空时，根据该方法自动映射
     * @param beanClass SearchBean 的 Class
     * @return {@link Table }，若返回 null，则表示 beanClass 不支持检索
     */
    Table table(Class<?> beanClass);

    /**
     * 根据 field 获取表列名
     * 在 SearchBean 的某字段上没加 {@link DbField } 注解，同时没加 {@link SearchBean } 注解
     * 或 {@link SearchBean#tables()} 属性为空 或指定了 {@link SearchBean#autoMapTo()} 属性时，根据该方法自动自动映射字段
     * v3.2.0 新增 beanClass 参数（因为 field 参数可能是父类的字段）
     * @param beanClass SearchBean 的 Class
     * @param field SearchBean 的字段
     * @return {@link Column }，若返回 null，则表示忽略该字段
     */
    Column column(Class<?> beanClass, Field field);

    /**
     * 表信息
     * @since v3.1.0
     */
    class Table {

        /**
         * 所属数据源
         */
        private final String dataSource;

        /**
         * 需要查询的数据表
         * */
        private final String tables;

        /**
         * Where 条件
         * */
        private final String where;

        /**
         * 分组字段
         * */
        private final String groupBy;

        /**
         * Having 字句
         */
        private final String having;

        /**
         * 是否 distinct 结果
         * */
        private final boolean distinct;

        /**
         * 默认排序信息
         */
        private final String orderBy;

        /**
         * 是否允许使用检索参数指定排序参数
         */
        private final boolean sortable;

        public Table(String tables) {
            this("", tables, "", "", "", false, "", true);
        }

        public Table(String dataSource, String tables, String where, String groupBy, String having,
                     boolean distinct, String orderBy, boolean sortable) {
            this.dataSource = dataSource;
            this.tables = tables;
            this.where = where;
            this.groupBy = groupBy;
            this.having = having;
            this.distinct = distinct;
            this.orderBy = orderBy;
            this.sortable = sortable;
        }

        public String getDataSource() {
            return dataSource;
        }

        public String getTables() {
            return tables;
        }

        public String getWhere() {
            return where;
        }

        public String getGroupBy() {
            return groupBy;
        }

        public String getHaving() {
            return having;
        }

        public boolean isDistinct() {
            return distinct;
        }

        public String getOrderBy() {
            return orderBy;
        }

        public boolean isSortable() {
            return sortable;
        }

    }

    /**
     * 列信息
     * @since v3.1.0
     */
    class Column {

        /**
         * 该字段对应的 SQL 片段
         */
        private final String fieldSql;

        /**
         * 该字段是否可作为检索参数
         */
        private final boolean conditional;

        /**
         * 该字段可作为检索时，被允许的运算符
         */
        private final Class<? extends FieldOp>[] onlyOn;

        /**
         * 字段别名
         * @since v3.5.0
         */
        private final String alias;

        /**
         * 数据库字段类型
         * @since v3.8.0
         */
        private final DbType dbType;

        public Column(String fieldSql, boolean conditional, Class<? extends FieldOp>[] onlyOn, DbType dbType) {
            this(fieldSql, conditional, onlyOn, null, dbType);
        }

        public Column(String fieldSql, boolean conditional, Class<? extends FieldOp>[] onlyOn, String alias, DbType dbType) {
            this.fieldSql = fieldSql;
            this.conditional = conditional;
            this.onlyOn = onlyOn;
            this.alias = alias;
            this.dbType = dbType;
        }

        public String getFieldSql() {
            return fieldSql;
        }

        public boolean isConditional() {
            return conditional;
        }

        public Class<? extends FieldOp>[] getOnlyOn() {
            return onlyOn;
        }

        public String getAlias() {
            return alias;
        }

        public DbType getDbType() {
            return dbType;
        }

    }

    /**
     * 数据库字段类型识别器
     * @since v3.8.0
     */
    interface DbTypeMapper {

        /**
         * @param fieldType 实体类字段类型
         * @return 对应的数据库字段类型（非空）
         */
        DbType map(Class<?> fieldType);

    }

}
