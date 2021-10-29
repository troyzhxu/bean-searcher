package com.ejlchina.searcher.beanmap;

import java.util.HashMap;
import java.util.Map;

import com.ejlchina.searcher.SearchResultConvertInfo;


/**
 * 用于缓存 @SearchBeanMap
 * 
 * @author Troy.Zhou @ 2017-03-20
 *
 */
public class SearchBeanCache {


	private static final Map<String, SearchBeanMap> cache = new HashMap<>();
	

	public static <T> void addSearchBeanMap(Class<T> beanClass, SearchBeanMap searchBeanMap) {
		SearchResultConvertInfo<T> convertInfo = new SearchResultConvertInfo<>();
		convertInfo.setFieldDbAliasEntrySet(searchBeanMap.getFieldDbAliasMap().entrySet());
		convertInfo.setFieldGetMethodMap(searchBeanMap.getFieldGetMethodMap());
		convertInfo.setFieldTypeMap(searchBeanMap.getFieldTypeMap());
		searchBeanMap.setConvertInfo(convertInfo);
		cache.put(beanClass.getName(), searchBeanMap);
	}
	
	
	public static SearchBeanMap getSearchBeanMap(Class<?> beanClass) {
		return cache.get(beanClass.getName());
	}
	
	
	public static void clear() {
		cache.clear();
	}
	
	
}
