package cn.zhxu.bs;

import cn.zhxu.bs.bean.*;
import cn.zhxu.bs.implement.BasePageExtractor;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

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
         * @see SearchBean#fields()
         * @since v4.1.0
         */
        private final List<Column> fields;

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

        /**
         * 单条 SQL 执行超时时间，单位：秒，0 表示永远不超时
         */
        private final int timeout;

        /**
         * 单页最大允许查询条数，0 或负数表示使用全局配置的默认值: {@link BasePageExtractor#getMaxAllowedSize()}
         * @since v4.5.0
         */
        private final int maxSize;

        /**
         * 最大允许偏移量（分页深度），0 或负数表示使用全局配置的默认值: {@link BasePageExtractor#getMaxAllowedOffset()}
         * @since v4.5.0
         */
        private final int maxOffset;

        public Table(String tables) {
            this("", tables, Collections.emptyList(), "", "", "", false, "", true, 0, 0, 0);
        }

        public Table(String dataSource, String tables, List<Column> fields, String where, String groupBy, String having,
                     boolean distinct, String orderBy, boolean sortable, int timeout, int maxSize, int maxOffset) {
            this.dataSource = dataSource;
            this.tables = tables;
            this.fields = fields;
            this.where = where;
            this.groupBy = groupBy;
            this.having = having;
            this.distinct = distinct;
            this.orderBy = orderBy;
            this.sortable = sortable;
            this.timeout = timeout;
            this.maxSize = maxSize;
            this.maxOffset = maxOffset;
        }

        public String getDataSource() {
            return dataSource;
        }

        public String getTables() {
            return tables;
        }

        public List<Column> getFields() {
            return fields;
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

        public int getTimeout() {
            return timeout;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public int getMaxOffset() {
            return maxOffset;
        }

    }

    /**
     * 列信息
     * @since v3.1.0
     */
    class Column {

        /**
         * 列名
         */
        private final String name;

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

        /**
         * 字段的聚合标志
         * @since v4.1.0
         */
        private final Cluster cluster;

        public Column(String name, String fieldSql, boolean conditional, Class<? extends FieldOp>[] onlyOn, DbType dbType) {
            this(name, fieldSql, conditional, onlyOn, null, dbType, Cluster.AUTO);
        }

        public Column(String name, String fieldSql, boolean conditional, Class<? extends FieldOp>[] onlyOn, String alias, DbType dbType, Cluster cluster) {
            this.name = name;
            this.fieldSql = fieldSql;
            this.conditional = conditional;
            this.onlyOn = onlyOn;
            this.alias = alias;
            this.dbType = dbType;
            this.cluster = cluster;
        }

        public String getName() {
            return name;
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

        public Cluster getCluster() {
            return cluster;
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
