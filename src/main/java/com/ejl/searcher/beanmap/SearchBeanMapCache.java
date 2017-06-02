package com.ejl.searcher.beanmap;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Troy.Zhou @ 2017-03-20
 *
 * 用于缓存 @SearchBeanMap
 * 
 */
public class SearchBeanMapCache {

	private static SearchBeanMapCache instance = new SearchBeanMapCache();
	
	private Map<Class<?>, SearchBeanMap> cache = new HashMap<>();
	
	
	public static SearchBeanMapCache sharedCache() {
		return instance;
	}
	
	
	public void addSearchBeanMap(Class<?> beanClass, SearchBeanMap dbMap) {
		cache.put(beanClass, dbMap);
	}
	
	
	public SearchBeanMap getSearchBeanMap(Class<?> beanClass) {
		return cache.get(beanClass);
	}
	
	
	public void clear() {
		cache.clear();
	}
	
	
}
