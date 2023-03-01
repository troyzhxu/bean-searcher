package cn.zhxu.bs.implement;

import cn.zhxu.bs.DbMapping;
import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SearchException;
import cn.zhxu.bs.bean.*;
import cn.zhxu.bs.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 默认的数据库映射解析器
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.1.0 从 DefaultMetaResolver 里分离出来
 */
public class DefaultDbMapping implements DbMapping {

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

    // 标识符的 围绕符，以区分系统保留字，只对自动映射的表名与字段起作用（since v4.0.0）
    private String aroundChar;

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
            return new Table(bean.dataSource().trim(),
                    tables(beanClass, bean),
                    bean.where().trim(),
                    bean.groupBy().trim(),
                    bean.having().trim(),
                    bean.distinct(),
                    bean.orderBy(),
                    sortable(bean.sortType()),
                    bean.timeout(),
                    columns(beanClass, bean.fields())
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

    interface BeanField {
        String getName();
        Class<?> getType();
        <T extends Annotation> T getAnnotation(Class<T> annotationClass);
        Class<?> getDeclaringClass();
    }

    protected List<Column> columns(Class<?> beanClass, DbField[] fields) {
        return Arrays.stream(fields).map(field -> column(beanClass, new BeanField() {
            @Override
            public String getName() {
                String name = field.name();
                if (StringUtils.isBlank(name)) {
                    throw new SearchException("The name of @DbField in @SearchBean.fields on [" + beanClass.getName() + "] is not assigned.");
                }
                return name;
            }
            @Override
            public Class<?> getType() {
                return null;
            }
            @Override
            @SuppressWarnings("all")
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
                return annotationClass == DbField.class ? (T) field : null;
            }
            @Override
            public Class<?> getDeclaringClass() {
                return beanClass;
            }
        })).collect(Collectors.toList());
    }

    @Override
    public Column column(Class<?> beanClass, Field field) {
        return column(beanClass, new BeanField() {
            @Override
            public String getName() {
                return field.getName();
            }
            @Override
            public Class<?> getType() {
                return field.getType();
            }
            @Override
            public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
                return field.getAnnotation(annotationClass);
            }
            @Override
            public Class<?> getDeclaringClass() {
                return field.getDeclaringClass();
            }
        });
    }

    protected Column column(Class<?> beanClass, BeanField field) {
        String fieldSql = dbFieldSql(beanClass, field);
        if (fieldSql == null) {
            return null;
        }
        Class<?> fieldType = field.getType();
        DbField dbField = field.getAnnotation(DbField.class);
        if (dbField != null) {
            DbType dbType = dbField.type();
            if (dbType == DbType.UNKNOWN && fieldType != null) {
                dbType = dbTypeMapper.map(fieldType);
            }
            String name = StringUtils.isBlank(dbField.name()) ? field.getName() : dbField.name();
            return new Column(name, fieldSql, dbField.conditional(), dbField.onlyOn(), dbField.alias(), dbType, dbField.cluster());
        }
        DbType dbType = fieldType != null ? dbTypeMapper.map(fieldType) : DbType.UNKNOWN;
        return new Column(field.getName(), fieldSql, true, EMPTY_OPERATORS, dbType);
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
            name = tablePrefix + name;
        }
        if (aroundChar != null) {
            name = aroundChar + name + aroundChar;
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

    protected String dbFieldSql(Class<?> beanClass, BeanField field) {
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
        String mapTo = dbField != null ? dbField.mapTo().trim() : null;
        if (dbField != null) {
            String fieldSql = dbField.value().trim();
            if (StringUtils.isNotBlank(fieldSql)) {
                if (fieldSql.toLowerCase().startsWith("select ")) {
                    return "(" + fieldSql + ")";
                }
                return withMapTo(fieldSql, mapTo);
            }
        } else if (shouldIgnore(field, ignoreFields)) {
            // 未加 @DbField 注解时，更据 ignoreFields 判断该字段是否应该被忽略
            return null;
        }
        if (StringUtils.isNotBlank(mapTo)) {
            // 指定了 mapTo 属性
            return withMapTo(toColumnName(field), mapTo);
        }
        String autoMapTo = bean != null ? bean.autoMapTo().trim() : null;
        if (StringUtils.isNotBlank(autoMapTo)) {
            // 指定了 autoMapTo, 则映射它指定的表
            return withMapTo(toColumnName(field), autoMapTo);
        }
        if (isMapToSingleTable(bean)) {
            // 单表映射
            return toColumnName(field);
        }
        // 其它情况，忽略该字段
        return null;
    }

    protected String withMapTo(String fieldSql, String mapTo) {
        if (StringUtils.isBlank(mapTo)) {
            return fieldSql;
        }
        return mapTo + "." + fieldSql;
    }

    // 判断是否是单表映射
    protected boolean isMapToSingleTable(SearchBean bean) {
        if (bean == null) return true;
        String tables = bean.tables().trim();
        return StringUtils.isBlank(tables) || SINGLE_TABLE_PATTERN.matcher(tables).matches();
    }

    // 比较 Field 的所在类层级是否比 @SearchBean 注解更接近父类
    // return 0 表示层级相等，
    // return + 表示 Field 比 @SearchBean 更接近父类
    // return - 表示 @SearchBean 比 Field 更接近父类
    protected int compareFieldToBeanAnnotation(BeanField field, Class<?> beanClass) {
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

    protected boolean shouldIgnore(BeanField field, String[] ignoreFields) {
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

    protected String toColumnName(BeanField field) {
        String name = field.getName();
        if (underlineCase) {
            name = StringUtils.toUnderline(name);
        }
        name = upperCase ? name.toUpperCase() : name;
        if (aroundChar != null) {
            name = aroundChar + name + aroundChar;
        }
        return name;
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

    public String getAroundChar() {
        return aroundChar;
    }

    public void setAroundChar(String aroundChar) {
        this.aroundChar = aroundChar;
    }

}
