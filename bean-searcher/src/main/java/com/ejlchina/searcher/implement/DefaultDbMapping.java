package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.DbMapping;
import com.ejlchina.searcher.SearchException;
import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.DbIgnore;
import com.ejlchina.searcher.bean.InheritType;
import com.ejlchina.searcher.bean.SearchBean;
import com.ejlchina.searcher.param.Operator;
import com.ejlchina.searcher.util.StringUtils;

import java.lang.reflect.Field;

/***
 * 默认的数据库映射解析器
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.1.0 从 DefaultMetaResolver 里分离出来
 */
public class DefaultDbMapping implements DbMapping {

    private static final Operator[] EMPTY_OPERATORS = {};

    // 表名前缀
    private String tablePrefix;

    // 表与列是否是大写风格
    private boolean upperCase = false;

    // 默认继承类型
    private InheritType defaultInheritType = InheritType.ALL;

    @Override
    public InheritType inheritType(Class<?> beanClass) {
        return defaultInheritType;
    }

    @Override
    public Table table(Class<?> beanClass) {
        SearchBean bean = beanClass.getAnnotation(SearchBean.class);
        if (bean != null) {
            return new Table(bean.dataSource().trim(),
                    tables(beanClass, bean),
                    bean.joinCond().trim(),
                    bean.groupBy().trim(),
                    bean.distinct()
            );
        }
        return new Table(null, toTableName(beanClass), "", "", false);
    }

    @Override
    public Column column(Field field) {
        String fieldSql = dbFieldSql(field);
        if (fieldSql == null) {
            return null;
        }
        DbField dbField = field.getAnnotation(DbField.class);
        if (dbField != null) {
            boolean conditional = dbField.conditional();
            Operator[] onlyOn = dbField.onlyOn();
            return new Column(fieldSql, conditional, onlyOn);
        }
        return new Column(fieldSql, true, EMPTY_OPERATORS);
    }

    protected String tables(Class<?> beanClass, SearchBean bean) {
        String tables = bean.tables();
        if (StringUtils.isBlank(tables)) {
            return toTableName(beanClass);
        }
        return tables.trim();
    }

    protected String toTableName(Class<?> beanClass) {
        String className = beanClass.getSimpleName();
        String tables = StringUtils.toUnderline(className);
        if (upperCase) {
            tables = tables.toUpperCase();
        }
        if (tablePrefix != null) {
            return tablePrefix + tables;
        }
        return tables;
    }

    protected String dbFieldSql(Field field) {
        Class<?> beanClass = field.getDeclaringClass();
        DbField dbField = field.getAnnotation(DbField.class);
        if (field.getAnnotation(DbIgnore.class) != null) {
            if (dbField == null) {
                return null;
            }
            throw new SearchException("[" + beanClass.getName() + ": " + field.getName() + "] is annotated by @DbField and @DbIgnore, which are mutually exclusive.");
        }
        if (dbField != null) {
            String fieldSql = dbField.value().trim();
            if (StringUtils.isNotBlank(fieldSql)) {
                if (fieldSql.toLowerCase().startsWith("select ")) {
                    return "(" + fieldSql + ")";
                }
                return fieldSql;
            }
        }
        SearchBean bean = beanClass.getAnnotation(SearchBean.class);
        // 没加 @SearchBean 注解，或者加了但没给 tables 赋值，则可以自动映射列名，因为此时默认为单表映射
        if (bean == null || StringUtils.isBlank(bean.tables())) {
            // 默认使用下划线风格的字段映射
            return toColumnName(field);
        }
        String tab = bean.autoMapTo();
        if (StringUtils.isBlank(tab)) {
            return null;
        }
        return tab.trim() + "." + toColumnName(field);
    }

    private String toColumnName(Field field) {
        String column = StringUtils.toUnderline(field.getName());
        return upperCase ? column.toUpperCase() : column;
    }

    public InheritType getDefaultInheritType() {
        return defaultInheritType;
    }

    public void setDefaultInheritType(InheritType defaultInheritType) {
        this.defaultInheritType = defaultInheritType;
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

}
