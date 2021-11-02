package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;
import com.ejlchina.searcher.param.Operator;
import com.ejlchina.searcher.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 默认元信息解析器
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.0.0
 */
public class DefaultMetadataResolver implements MetadataResolver {

    private static final Operator[] EMPTY_OPERATORS = {};

    private final Map<Class<?>, BeanMeta<?>> cache = new ConcurrentHashMap<>();

    private SnippetResolver snippetResolver = new DefaultSnippetResolver();

    private TableMapping tableMapping = new TableMapping() {

        @Override
        public String toTableName(Class<?> beanClass) {
            // 默认使用连字符风格的表名映射
            return StringUtils.toUnderline(beanClass.getSimpleName());
        }

        @Override
        public String toColumnName(Field field) {
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
                snippetResolver.resolve(tables(beanClass, bean)),
                snippetResolver.resolve(joinCond(bean)),
                snippetResolver.resolve(groupBy(bean)),
                bean != null && bean.distinct());
        // 字段解析
        Field[] fields = beanClass.getDeclaredFields();
        for (int index = 0; index < fields.length; index++) {
            Field field = fields[index];
            String fieldSql = dbFieldSql(bean, field);
            if (fieldSql == null) {
                continue;
            }
            SqlSnippet fieldSnippet = snippetResolver.resolve(fieldSql);
            FieldMeta fieldMeta = resolveFieldMeta(beanClass, field, fieldSnippet, index);
            beanMeta.addFieldMeta(field.getName(), fieldMeta);
        }
        if (beanMeta.getFieldList().size() == 0) {
            throw new SearchException("[" + beanClass.getName() + "] is not a valid SearchBean, because there is none field mapping to database.");
        }
        return beanMeta;
    }

    protected FieldMeta resolveFieldMeta(Class<?> beanClass, Field field, SqlSnippet snippet, int index) {
        Method setter = getSetterMethod(beanClass, field);
        String dbAlias = "_" + index;
        DbField dbField = field.getAnnotation(DbField.class);
        boolean conditional = dbField == null || dbField.conditional();
        Operator[] onlyOn = dbField != null ? dbField.onlyOn() : EMPTY_OPERATORS;
        return new FieldMeta(field.getName(), field.getType(), setter, snippet, dbAlias, conditional, onlyOn);
    }

    protected Method getSetterMethod(Class<?> beanClass, Field field) {
        String fieldName = field.getName();
        try {
            return beanClass.getMethod("set" + StringUtils.firstCharToUpperCase(fieldName), field.getType());
        } catch (Exception e) {
            throw new SearchException("[" + beanClass.getName() + ": " + fieldName + "] is annotated by @DbField, but there is none correctly setter for it.", e);
        }
    }

    protected String tables(Class<?> beanClass, SearchBean bean) {
        if (bean == null || StringUtils.isBlank(bean.tables())) {
            return tableMapping.toTableName(beanClass);
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
        if (dbField != null) {
            String fieldSql = dbField.value().trim();
            if (StringUtils.isNotBlank(fieldSql)) {
                if (fieldSql.toLowerCase().startsWith("select ")) {
                    return "(" + fieldSql + ")";
                }
                return fieldSql;
            }
        }
        // 没加 @SearchBean 注解，或者加了但没给 tables 赋值，则可以自动映射列名，因为此时默认为单表映射
        if (bean == null || StringUtils.isBlank(bean.tables())) {
            return tableMapping.toColumnName(field);
        }
        String tab = bean.autoMapTo();
        if (StringUtils.isBlank(tab)) {
            return null;
        }
        String column = tableMapping.toColumnName(field);
        return tab.trim() + "." + column;
    }

    public SnippetResolver getSnippetResolver() {
        return snippetResolver;
    }

    public void setSnippetResolver(SnippetResolver snippetResolver) {
        this.snippetResolver = Objects.requireNonNull(snippetResolver);
    }

    public TableMapping getTableMapping() {
        return tableMapping;
    }

    public void setTableMapping(TableMapping tableMapping) {
        this.tableMapping = Objects.requireNonNull(tableMapping);
    }

}
