package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.DbIgnore;
import com.ejlchina.searcher.bean.SearchBean;
import com.ejlchina.searcher.param.Operator;
import com.ejlchina.searcher.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 默认元信息解析器
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.0.0
 */
public class DefaultMetaResolver implements MetaResolver {

    private static final Operator[] EMPTY_OPERATORS = {};

    private final Map<Class<?>, BeanMeta<?>> cache = new ConcurrentHashMap<>();

    private SnippetResolver snippetResolver = new DefaultSnippetResolver();

    private DbMapping dbMapping = new DbMapping() {

        @Override
        public String table(Class<?> beanClass) {
            // 默认使用连字符风格的表名映射
            return StringUtils.toUnderline(beanClass.getSimpleName());
        }

        @Override
        public String column(Field field) {
            // 默认使用连字符风格的字段映射
            return StringUtils.toUnderline(field.getName());
        }

    };

    @Override
    public <T> BeanMeta<T> resolve(Class<T> beanClass) {
        @SuppressWarnings("unchecked")
        BeanMeta<T> beanMeta = (BeanMeta<T>) cache.get(beanClass);
        if (beanMeta != null) {
            return beanMeta;
        }
        synchronized (cache) {
            beanMeta = resolveMetadata(beanClass);
            cache.put(beanClass, beanMeta);
            return beanMeta;
        }
    }

    protected <T> BeanMeta<T> resolveMetadata(Class<T> beanClass) {
        SearchBean bean = beanClass.getAnnotation(SearchBean.class);
        // v3.0.0 后 bean 可以为 null
        BeanMeta<T> beanMeta = new BeanMeta<>(beanClass,
                bean != null ? bean.dataSource().trim() : null,
                snippetResolver.resolve(tables(beanClass, bean)),
                snippetResolver.resolve(joinCond(bean)),
                snippetResolver.resolve(groupBy(bean)),
                bean != null && bean.distinct());
        // 字段解析
        Field[] fields = beanClass.getDeclaredFields();
        for (int index = 0; index < fields.length; index++) {
            Field field = fields[index];
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            String fieldSql = dbFieldSql(bean, field);
            if (fieldSql == null) {
                continue;
            }
            SqlSnippet fieldSnippet = snippetResolver.resolve(fieldSql);
            FieldMeta fieldMeta = resolveFieldMeta(beanMeta, field, fieldSnippet, index);
            beanMeta.addFieldMeta(field.getName(), fieldMeta);
        }
        if (beanMeta.getFieldCount() == 0) {
            throw new SearchException("[" + beanClass.getName() + "] is not a valid SearchBean, because there is no field mapping to database.");
        }
        return beanMeta;
    }

    protected FieldMeta resolveFieldMeta(BeanMeta<?> beanMeta, Field field, SqlSnippet snippet, int index) {
        Class<?> beanClass = beanMeta.getBeanClass();
        Method setter = getSetterMethod(beanClass, field);
        String dbAlias = "c_" + index;          // 注意：Oracle 数据库的别名不能以下划线开头
        DbField dbField = field.getAnnotation(DbField.class);
        boolean conditional = dbField == null || dbField.conditional();
        Operator[] onlyOn = dbField != null ? dbField.onlyOn() : EMPTY_OPERATORS;
        return new FieldMeta(beanMeta, field, setter, snippet, dbAlias, conditional, onlyOn);
    }

    protected Method getSetterMethod(Class<?> beanClass, Field field) {
        String fieldName = field.getName();
        try {
            return beanClass.getMethod("set" + StringUtils.firstCharToUpperCase(fieldName), field.getType());
        } catch (Exception e) {
            throw new SearchException("[" + beanClass.getName() + ": " + fieldName + "] is annotated by @DbField, but there is no correctly setter for it.", e);
        }
    }

    protected String tables(Class<?> beanClass, SearchBean bean) {
        if (bean == null || StringUtils.isBlank(bean.tables())) {
            String table = dbMapping.table(beanClass);
            if (StringUtils.isBlank(table)) {
                throw new SearchException("The class [" + beanClass.getName()
                        + "] can not be searched, because there is no table mapping provided by @SearchBean and DbMapping");
            }
            return table.trim();
        }
        return bean.tables().trim();
    }

    protected String joinCond(SearchBean bean) {
        return bean != null ? bean.joinCond().trim() : "";
    }

    protected String groupBy(SearchBean bean) {
        return bean != null ? bean.groupBy().trim() : "";
    }

    protected String dbFieldSql(SearchBean bean, Field field) {
        DbField dbField = field.getAnnotation(DbField.class);
        boolean dbIgnore = field.getAnnotation(DbIgnore.class) != null;
        if (dbField != null) {
            if (dbIgnore) {
                throw new SearchException("[" + field.getDeclaringClass().getName() + ": " + field.getName() + "] is annotated by @DbField and @DbIgnore, which are mutually exclusive.");
            }
            String fieldSql = dbField.value().trim();
            if (StringUtils.isNotBlank(fieldSql)) {
                if (fieldSql.toLowerCase().startsWith("select ")) {
                    return "(" + fieldSql + ")";
                }
                return fieldSql;
            }
        }
        if (dbIgnore) {
            return null;
        }
        // 没加 @SearchBean 注解，或者加了但没给 tables 赋值，则可以自动映射列名，因为此时默认为单表映射
        if (bean == null || StringUtils.isBlank(bean.tables())) {
            return getColumn(field);
        }
        String tab = bean.autoMapTo();
        if (StringUtils.isBlank(tab)) {
            return null;
        }
        String column = getColumn(field);
        if (column == null) {
            return null;
        }
        return tab.trim() + "." + column;
    }

    private String getColumn(Field field) {
        String column = dbMapping.column(field);
        if (StringUtils.isBlank(column)) {
            return null;
        }
        return column.trim();
    }

    public SnippetResolver getSnippetResolver() {
        return snippetResolver;
    }

    public void setSnippetResolver(SnippetResolver snippetResolver) {
        this.snippetResolver = Objects.requireNonNull(snippetResolver);
    }

    public DbMapping getDbMapping() {
        return dbMapping;
    }

    public void setDbMapping(DbMapping dbMapping) {
        this.dbMapping = Objects.requireNonNull(dbMapping);
    }

}
