package com.ejlchina.searcher.beanmap;

import java.util.HashMap;
import java.util.Map;


/**
 * 用于缓存 @SearchBeanMap
 * 
 * @author Troy.Zhou @ 2017-03-20
 *
 */
public class SearchBeanMapCache {

	private static SearchBeanMapCache instance = new SearchBeanMapCache();
	
	private Map<String, SearchBeanMap> cache = new HashMap<>();
	
	
	public static SearchBeanMapCache sharedCache() {
		return instance;
	}
	
	
	public void addSearchBeanMap(Class<?> beanClass, SearchBeanMap dbMap) {
		cache.put(beanClass.getName(), dbMap);
	}
	
	
	public SearchBeanMap getSearchBeanMap(Class<?> beanClass) {
		return cache.get(beanClass.getName());
	}
	
	
	public void clear() {
		cache.clear();
	}
	
	
}
