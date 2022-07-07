package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.bean.*;
import com.ejlchina.searcher.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 默认的数据库映射解析器
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.1.0 从 DefaultMetaResolver 里分离出来
 */
public class DefaultDbMapping implements DbMapping {

    protected static final Logger log = LoggerFactory.getLogger(DefaultDbMapping.class);

    @SuppressWarnings("unchecked")
    protected static final Class<FieldOp>[] EMPTY_OPERATORS = new Class[0];

    protected static final Pattern SINGLE_TABLE_PATTERN = Pattern.compile("\\w+|\\w+\\s+\\w+");

    // since v3.8.0
    private DbTypeMapper dbTypeMapper = new DefaultDbTypeMapper();

    // 表名前缀（since v3.1.0）
    private String tablePrefix;

    // 表与列是否是大写风格（since v3.1.0）
    private boolean upperCase = false;

    // 驼峰是否转下划线（since v3.7.0）
    private boolean underlineCase = true;

    // 默认继承类型（since v3.2.0）
    private InheritType defaultInheritType = InheritType.ALL;

    // 默认的排序约束类型
    private SortType defaultSortType = SortType.ALLOW_PARAM;

    // 冗余的后缀（如果类名已这些后缀结尾，将自动去掉这些后缀）（since v3.3.0）
    private String[] redundantSuffixes;

    // 全局忽略的实体类属性名（since v3.4.0）
    private String[] ignoreFields;

    @Override
    public InheritType inheritType(Class<?> beanClass) {
        SearchBean bean = getSearchBean(beanClass);
        if (bean != null) {
            InheritType iType = bean.inheritType();
            if (iType != InheritType.DEFAULT) {
                return iType;
            }
        }
        return defaultInheritType;
    }

    @Override
    public Table table(Class<?> beanClass) {
        SearchBean bean = getSearchBean(beanClass);
        if (bean != null) {
            String where = bean.where().trim();
            if (StringUtils.isBlank(where)) {
                where = bean.joinCond().trim();
                if (StringUtils.isNotBlank(where)) {
                    log.warn("@SearchBean.joinCond was deprecated, please use @SearchBean.where instead.");
                }
            }
            return new Table(bean.dataSource().trim(),
                    tables(beanClass, bean),
                    where,
                    bean.groupBy().trim(),
                    bean.having().trim(),
                    bean.distinct(),
                    bean.orderBy(),
                    sortable(bean.sortType())
            );
        }
        return new Table(toTableName(beanClass));
    }

    protected boolean sortable(SortType sortType) {
        if (sortType == SortType.ALLOW_PARAM) {
            return true;
        }
        if (sortType == SortType.ONLY_ENTITY) {
            return false;
        }
        return defaultSortType == SortType.ALLOW_PARAM;
    }

    @Override
    public Column column(Class<?> beanClass, Field field) {
        String fieldSql = dbFieldSql(beanClass, field);
        if (fieldSql == null) {
            return null;
        }
        DbField dbField = field.getAnnotation(DbField.class);
        if (dbField != null) {
            DbType dbType = dbField.type();
            if (dbType == DbType.UNKNOWN) {
                dbType = dbTypeMapper.map(field.getType());
            }
            Class<? extends Convertor> convClazz = dbField.converter() != Convertor.class ? dbField.converter() : null;
            return new Column(fieldSql, dbField.conditional(), dbField.onlyOn(), dbField.alias(), dbType, convClazz);
        }
        DbType dbType = dbTypeMapper.map(field.getType());
        return new Column(fieldSql, true, EMPTY_OPERATORS, dbType);
    }

    protected SearchBean getSearchBean(Class<?> beanClass) {
        while (beanClass != Object.class) {
            SearchBean bean = beanClass.getAnnotation(SearchBean.class);
            if (bean != null) {
                return bean;
            }
            if (defaultInheritType != InheritType.TABLE && defaultInheritType != InheritType.ALL) {
                break;
            }
            beanClass = beanClass.getSuperclass();
        }
        return null;
    }

    protected String tables(Class<?> beanClass, SearchBean bean) {
        String tables = bean.tables();
        if (StringUtils.isBlank(tables)) {
            return toTableName(beanClass);
        }
        return tables.trim();
    }

    protected String toTableName(Class<?> beanClass) {
        String name = simplify(beanClass.getSimpleName());
        if (underlineCase) {
            name = StringUtils.toUnderline(name);
        }
        if (upperCase) {
            name = name.toUpperCase();
        }
        if (tablePrefix != null) {
            return tablePrefix + name;
        }
        return name;
    }

    protected String simplify(String className) {
        if (redundantSuffixes != null) {
            int length = className.length();
            for (String suffix: redundantSuffixes) {
                if (length > suffix.length() && className.endsWith(suffix)) {
                    return className.substring(0, length - suffix.length());
                }
            }
        }
        return className;
    }

    protected String dbFieldSql(Class<?> beanClass, Field field) {
        DbField dbField = field.getAnnotation(DbField.class);
        if (field.getAnnotation(DbIgnore.class) != null) {
            if (dbField == null) {
                return null;
            }
            throw new SearchException("[" + beanClass.getName() + ": " + field.getName() + "] is annotated by @DbField and @DbIgnore, which are mutually exclusive.");
        }
        SearchBean bean = getSearchBean(beanClass);
        // 判断是否在 @SearchBean 注解中忽略了该字段
        if (bean != null && shouldIgnore(field, bean.ignoreFields())) {
            if (dbField == null) {
                return null;
            }
            // 如果该属性同时被 @DbFeild 注解了，则判断 @SearchBean 与 @DbFeild 所在的类层级，子类优先父类
            int res = compareFieldToBeanAnnotation(field, beanClass);
            if (res == 0) {
                throw new SearchException("[" + beanClass.getName() + ": " + field.getName() + "] is annotated by @DbField and listed by @SearchBean.ignoreFields in same class, which are mutually exclusive.");
            }
            if (res > 0) {
                return null;
            }
        }
        if (dbField != null) {
            String fieldSql = dbField.value().trim();
            if (StringUtils.isNotBlank(fieldSql)) {
                if (fieldSql.toLowerCase().startsWith("select ")) {
                    return "(" + fieldSql + ")";
                }
                return fieldSql;
            }
        } else if (shouldIgnore(field, ignoreFields)) {
            // 未加 @DbField 注解时，更据 ignoreFields 判断该字段是否应该被忽略
            return null;
        }
        if (bean == null) {
            // 省略了 @SearchBean 注解，此时默认为单表映射
            return toColumnName(field);
        }
        String tables = bean.tables().trim();
        if (StringUtils.isBlank(tables)) {
            // 注解 @SearchBean 的 tables 没有赋值，此时默认为单表映射
            return toColumnName(field);
        }
        String tab = bean.autoMapTo().trim();
        if (StringUtils.isNotBlank(tab)) {
            // 指定了 autoMapTo, 则映射它指定的表
            return tab + "." + toColumnName(field);
        }
        Matcher matcher = SINGLE_TABLE_PATTERN.matcher(tables);
        if (matcher.matches()) {
            // @SearchBean.tables 是单表
            return toColumnName(field);
        }
        return null;
    }

    // 比较 Field 的所在类层级是否比 @SearchBean 注解更接近父类
    // return 0 表示层级相等，
    // return + 表示 Field 比 @SearchBean 更接近父类
    // return - 表示 @SearchBean 比 Field 更接近父类
    protected int compareFieldToBeanAnnotation(Field field, Class<?> beanClass) {
        int fieldLevel = 0;
        int beanLevel = 0;
        Class<?> clazz = beanClass;
        while (clazz != field.getDeclaringClass() && clazz != Object.class) {
            clazz = clazz.getSuperclass();
            fieldLevel++;
        }
        clazz = beanClass;
        while (clazz.getAnnotation(SearchBean.class) == null && clazz != Object.class) {
            clazz = clazz.getSuperclass();
            beanLevel++;
        }
        return fieldLevel - beanLevel;
    }

    protected boolean shouldIgnore(Field field, String[] ignoreFields) {
        if (ignoreFields != null) {
            String name = field.getName();
            for (String igField : ignoreFields) {
                if (name.equals(igField)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String toColumnName(Field field) {
        String name = field.getName();
        if (underlineCase) {
            name = StringUtils.toUnderline(name);
        }
        return upperCase ? name.toUpperCase() : name;
    }

    public DbTypeMapper getDbTypeMapper() {
        return dbTypeMapper;
    }

    public void setDbTypeMapper(DbTypeMapper dbTypeMapper) {
        this.dbTypeMapper = Objects.requireNonNull(dbTypeMapper);
    }

    public InheritType getDefaultInheritType() {
        return defaultInheritType;
    }

    public void setDefaultInheritType(InheritType inheritType) {
        this.defaultInheritType = Objects.requireNonNull(inheritType);
    }

    public SortType getDefaultSortType() {
        return defaultSortType;
    }

    public void setDefaultSortType(SortType defaultSortType) {
        this.defaultSortType = Objects.requireNonNull(defaultSortType);
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix)) {
            this.tablePrefix = tablePrefix.trim();
        }
    }

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

}
