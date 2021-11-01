package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;
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

    private final Map<Class<?>, Metadata<?>> cache = new ConcurrentHashMap<>();

    private TableMapping tableMapping = new TableMapping();

    private SnippetResolver snippetResolver = new DefaultSnippetResolver();


    @Override
    public <T> Metadata<T> resolve(Class<T> beanClass) {
        @SuppressWarnings("unchecked")
        Metadata<T> metadata = (Metadata<T>) cache.get(beanClass);
        if (metadata != null) {
            return metadata;
        }
        synchronized (cache) {
            metadata = resolveMetadata(beanClass);
            cache.put(beanClass, metadata);
            return metadata;
        }
    }

    protected String dbField(SearchBean bean, Field field) {
        DbField dbField = field.getAnnotation(DbField.class);
        if (dbField != null) {
            return dbField.value().trim();
        }
        // 没加 @SearchBean 注解，或者加了但没给 tables 赋值，则可以自动映射列名，因为此时默认为单表映射
        if (bean == null || StringUtils.isBlank(bean.tables())) {
            return tableMapping.columnName(field);
        }
        String tab = bean.autoMapTab();
        if (StringUtils.isBlank(tab)) {
            return null;
        }
        String column = tableMapping.columnName(field);
        return tab.trim() + "." + column;
    }

    protected <T> Metadata<T> resolveMetadata(Class<T> beanClass) {
        SearchBean bean = beanClass.getAnnotation(SearchBean.class);
        // v3.0.0 后 bean 可以为 null
        Metadata<T> metadata = new Metadata<>(beanClass,
                snippetResolver.resolve(tables(beanClass, bean)),
                snippetResolver.resolve(joinCond(bean)),
                snippetResolver.resolve(groupBy(bean)),
                bean != null && bean.distinct());
        // 字段解析
        for (Field field : beanClass.getDeclaredFields()) {
            String dbField = dbField(bean, field);
            if (dbField == null) {
                continue;
            }
            SqlSnippet fieldSnippet = snippetResolver.resolve(dbField);
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            try {
                Method method = beanClass.getMethod("set" + StringUtils.firstCharToUpperCase(fieldName), fieldType);
                metadata.addFieldDbMap(fieldName, fieldSnippet, method, fieldType);
            } catch (Exception e) {
                throw new SearchException("[" + beanClass.getName() + ": " + fieldName + "] is annotated by @DbField, but there is none correctly setter for it.", e);
            }
        }
        if (metadata.getFieldList().size() == 0) {
            throw new SearchException("[" + beanClass.getName() + "] is annotated by @SearchBean, but there is none field annotated by @DbFile.");
        }
        // 对字段进行排序
        metadata.getFieldList().sort(String::compareTo);
        return metadata;
    }

    protected String tables(Class<?> beanClass, SearchBean bean) {
        if (bean == null || StringUtils.isBlank(bean.tables())) {
            return tableMapping.tableName(beanClass);
        }
        return bean.tables().trim();
    }

    protected String joinCond(SearchBean bean) {
        return bean != null ? bean.joinCond().trim() : "";
    }

    protected String groupBy(SearchBean bean) {
        return bean != null ? bean.groupBy().trim() : "";
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
        this.tableMapping = tableMapping;
    }

}
