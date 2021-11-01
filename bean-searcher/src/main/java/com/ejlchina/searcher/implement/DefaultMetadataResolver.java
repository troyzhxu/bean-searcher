package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;
import com.ejlchina.searcher.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
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

    private <T> Metadata<T> resolveMetadata(Class<T> beanClass) {
        SearchBean searchBean = beanClass.getAnnotation(SearchBean.class);
        if (searchBean == null) {
            throw new SearchException("The class [" + beanClass.getName()
                    + "] is not a valid SearchBean, please check whether the class is annotated correctly by @SearchBean");
        }
        SqlSnippet tableSnippet = snippetResolver.resolve(searchBean.tables().trim());
        SqlSnippet joinCondSnippet = snippetResolver.resolve(searchBean.joinCond().trim());
        SqlSnippet groupBySnippet = snippetResolver.resolve(searchBean.groupBy().trim());

        Metadata<T> metadata = new Metadata<>(beanClass, tableSnippet, joinCondSnippet, groupBySnippet, searchBean.distinct());

        for (Field field : beanClass.getDeclaredFields()) {
            DbField dbField = field.getAnnotation(DbField.class);
            if (dbField == null) {
                continue;
            }
            SqlSnippet fieldSnippet = snippetResolver.resolve(dbField.value().trim());
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

    public SnippetResolver getSnippetResolver() {
        return snippetResolver;
    }

    public void setSnippetResolver(SnippetResolver snippetResolver) {
        this.snippetResolver = Objects.requireNonNull(snippetResolver);
    }

}
