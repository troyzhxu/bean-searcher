package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.Metadata;
import com.ejlchina.searcher.MetadataResolver;
import com.ejlchina.searcher.SearchResultConvertInfo;
import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;
import com.ejlchina.searcher.util.StringUtils;
import com.ejlchina.searcher.virtual.DefaultVirtualParamProcessor;
import com.ejlchina.searcher.virtual.VirtualParamProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * @author Troy.Zhou @ 2021-10-30
 *
 * 默认元信息解析器
 */
public class DefaultMetadataResolver implements MetadataResolver {

    private final Map<Class<?>, Metadata> cache = new ConcurrentHashMap<>();

    private VirtualParamProcessor virtualParamProcessor = new DefaultVirtualParamProcessor();

    @Override
    public Metadata resolve(Class<?> beanClass) {
        Metadata metadata = cache.get(beanClass);
        if (metadata != null) {
            return metadata;
        }
        synchronized (cache) {
            metadata = resolveMetadata(beanClass);
            addSearchBeanMap(beanClass, metadata);
            return metadata;
        }
    }

    private Metadata resolveMetadata(Class<?> beanClass) {
        SearchBean searchBean = beanClass.getAnnotation(SearchBean.class);
        if (searchBean == null) {
            throw new SearcherException("The class [" + beanClass.getName()
                    + "] is not a valid SearchBean, please check whether the class is annotated correctly by @SearchBean");
        }
        Metadata metadata = new Metadata(searchBean.tables(), searchBean.joinCond(),
                searchBean.groupBy(), searchBean.distinct());
        for (Field field : beanClass.getDeclaredFields()) {
            DbField dbField = field.getAnnotation(DbField.class);
            if (dbField == null) {
                continue;
            }
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            try {
                Method method = beanClass.getMethod("set" + StringUtils.firstCharToUpperCase(fieldName), fieldType);
                metadata.addFieldDbMap(fieldName, dbField.value().trim(), method, fieldType);
            } catch (Exception e) {
                throw new SearcherException("[" + beanClass.getName() + ": " + fieldName + "] is annotated by @DbField, but there is none correctly setter for it.", e);
            }
        }
        if (metadata.getFieldList().size() == 0) {
            throw new SearcherException("[" + beanClass.getName() + "] is annotated by @SearchBean, but there is none field annotated by @DbFile.");
        }
        return virtualParamProcessor.process(metadata);
    }

    protected <T> void addSearchBeanMap(Class<T> beanClass, Metadata metadata) {
        SearchResultConvertInfo<T> convertInfo = new SearchResultConvertInfo<>(beanClass);
        convertInfo.setFieldDbAliasEntrySet(metadata.getFieldDbAliasMap().entrySet());
        convertInfo.setFieldGetMethodMap(metadata.getFieldGetMethodMap());
        convertInfo.setFieldTypeMap(metadata.getFieldTypeMap());
        metadata.setConvertInfo(convertInfo);
        cache.put(beanClass, metadata);
    }

    public VirtualParamProcessor getVirtualParamProcessor() {
        return virtualParamProcessor;
    }

    public void setVirtualParamProcessor(VirtualParamProcessor virtualParamProcessor) {
        this.virtualParamProcessor = virtualParamProcessor;
    }

}
