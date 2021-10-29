package com.ejlchina.searcher.beanmap;

import com.ejlchina.searcher.SearchResultConvertInfo;
import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;
import com.ejlchina.searcher.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 用于缓存 @SearchBeanMap
 * 
 * @author Troy.Zhou @ 2017-03-20
 *
 */
public class SearchBeanCache {


	private static final Map<String, SearchBeanMap> cache = new ConcurrentHashMap<>();
	

	public static <T> void addSearchBeanMap(Class<T> beanClass, SearchBeanMap searchBeanMap) {
		SearchResultConvertInfo<T> convertInfo = new SearchResultConvertInfo<>();
		convertInfo.setFieldDbAliasEntrySet(searchBeanMap.getFieldDbAliasMap().entrySet());
		convertInfo.setFieldGetMethodMap(searchBeanMap.getFieldGetMethodMap());
		convertInfo.setFieldTypeMap(searchBeanMap.getFieldTypeMap());
		searchBeanMap.setConvertInfo(convertInfo);
		cache.put(beanClass.getName(), searchBeanMap);
	}
	
	
	public static SearchBeanMap getSearchBeanMap(Class<?> beanClass) {
		SearchBeanMap beanMap = cache.get(beanClass.getName());
		if (beanMap != null) {
			return beanMap;
		}
		SearchBean searchBean = beanClass.getAnnotation(SearchBean.class);
		if (searchBean == null) {
			throw new SearcherException("The class [" + beanClass.getName()
					+ "] is not a valid SearchBean, please check whether the class is annotated correctly by @SearchBean");
		}
		synchronized (SearchBeanCache.class) {
			SearchBeanMap searchBeanMap = new SearchBeanMap(searchBean.tables(), searchBean.joinCond(),
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
					searchBeanMap.addFieldDbMap(fieldName, dbField.value().trim(), method, fieldType);
				} catch (Exception e) {
					throw new SearcherException("[" + beanClass.getName() + ": " + fieldName + "] is annotated by @DbField, but there is none correctly setter for it.", e);
				}
			}
			if (searchBeanMap.getFieldList().size() == 0) {
				throw new SearcherException("[" + beanClass.getName() + "] is annotated by @SearchBean, but there is none field annotated by @DbFile.");
			}
			SearchBeanCache.addSearchBeanMap(beanClass, searchBeanMap);
			return searchBeanMap;
		}
	}
	
	
	public static void clear() {
		cache.clear();
	}
	
	
}
