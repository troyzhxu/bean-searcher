package cn.zhxu.bs.solon.prop;

import cn.zhxu.bs.bean.DbField;
import cn.zhxu.bs.bean.InheritType;
import cn.zhxu.bs.bean.SearchBean;
import cn.zhxu.bs.bean.SortType;

import java.util.HashMap;
import java.util.Map;

public class Sql {

    public enum Dialect {
        MySQL,
        Oracle,
        PostgreSQL,
        /**
         * alias for PostgreSQL
         */
        PgSQL,
        SqlServer
    }

    /**
     * 数据库方言，可选：MySQL、Oracle、PostgreSql，默认为 MySQL，另可通过声明 Spring Bean 来使用其它自定义方言
     */
    private Dialect dialect = Dialect.MySQL;

    /**
     * 是否启用动态方言
     */
    private boolean dialectDynamic;

    /**
     * 多方言配置：数据源名称 -> 方言类型
     */
    private Map<String, Dialect> dialects = new HashMap<>();

    /**
     * 默认映射配置
     */
    private final DefaultMapping defaultMapping = new DefaultMapping();

    /**
     * 慢 SQL 阈值（单位：毫秒），默认：500 毫秒
     * @since v3.7.0
     */
    private long slowSqlThreshold = 500;

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public boolean isDialectDynamic() {
        return dialectDynamic;
    }

    public void setDialectDynamic(boolean dialectDynamic) {
        this.dialectDynamic = dialectDynamic;
    }

    public Map<String, Dialect> getDialects() {
        return dialects;
    }

    public void setDialects(Map<String, Dialect> dialects) {
        this.dialects = dialects;
    }

    public DefaultMapping getDefaultMapping() {
        return defaultMapping;
    }

    public static class DefaultMapping {

        /**
         * 是否启动大写映射，启用后，自动映射出的表名与列名都是大写形式，默认为 false，
         * 注意：使用 {@link SearchBean#tables() } 与 {@link DbField#value() } 显示指定的表名与列表仍保持原有大小写形式
         */
        private boolean upperCase = false;

        /**
         * 驼峰是否转下划线，启用后，自动映射出的表名与列名都是下划线风格，默认为 true，
         * 注意：使用 {@link SearchBean#tables() } 与 {@link DbField#value() } 显示指定的表名与列表仍保持原有大小写形式
         */
        private boolean underlineCase = true;

        /**
         * 表名前缀，在自动映射表名时使用（即：当实体类没有用 {@link SearchBean#tables() } 指定表名时，框架会用该前缀与实体类名称自动生成一个表名），无默认值
         */
        private String tablePrefix = null;

        /**
         * 实体类的冗余后缀，在自动映射表名时使用，即：当框架用实体类名称自动生成一个表名时，会自动忽略实体类的后缀，如 VO，DTO 等，无默认值
         */
        private String[] redundantSuffixes;

        /**
         * 需要全局忽略的实体类属性名列表，无默认值，注意：如果属性添加的 {@link DbField } 注解，则不受该配置影响
         */
        private String[] ignoreFields;

        /**
         * 全局实体类继承机制，可选：`NONE`、`TABLE`、`FIELD`、`ALL`，默认为 `ALL`，注意：该配置的优先级比 {@link SearchBean#inheritType()} 低
         */
        private InheritType inheritType = InheritType.ALL;

        /**
         * 全局排序策略，可选：`ONLY_ENTITY`、`ALLOW_PARAM`，默认为 `ALLOW_PARAM`，注意：该配置的优先级比 {@link SearchBean#sortType()} 低
         */
        private SortType sortType = SortType.ALLOW_PARAM;

        /**
         * 标识符的 围绕符，以区分系统保留字，只对自动映射的表名与字段起作用（since v4.0.0）
         */
        private String aroundChar;

        public boolean isUpperCase() {
            return upperCase;
        }

        public void setUpperCase(boolean upperCase) {
            this.upperCase = upperCase;
        }

        public boolean isUnderlineCase() {
            return underlineCase;
        }

        public void setUnderlineCase(boolean underlineCase) {
            this.underlineCase = underlineCase;
        }

        public String getTablePrefix() {
            return tablePrefix;
        }

        public void setTablePrefix(String tablePrefix) {
            this.tablePrefix = tablePrefix;
        }

        public String[] getRedundantSuffixes() {
            return redundantSuffixes;
        }

        public void setRedundantSuffixes(String[] redundantSuffixes) {
            this.redundantSuffixes = redundantSuffixes;
        }

        public String[] getIgnoreFields() {
            return ignoreFields;
        }

        public void setIgnoreFields(String[] ignoreFields) {
            this.ignoreFields = ignoreFields;
        }

        public InheritType getInheritType() {
            return inheritType;
        }

        public void setInheritType(InheritType inheritType) {
            this.inheritType = inheritType;
        }

        public SortType getSortType() {
            return sortType;
        }

        public void setSortType(SortType sortType) {
            this.sortType = sortType;
        }

        public String getAroundChar() {
            return aroundChar;
        }

        public void setAroundChar(String aroundChar) {
            this.aroundChar = aroundChar;
        }

    }

    public long getSlowSqlThreshold() {
        return slowSqlThreshold;
    }

    public void setSlowSqlThreshold(long slowSqlThreshold) {
        this.slowSqlThreshold = slowSqlThreshold;
    }

}
