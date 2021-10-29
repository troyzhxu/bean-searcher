package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;

import java.util.List;
import java.util.Map;

/***
 * @author Troy.Zhou @ 2021-10-29
 * 
 * 自动检索器 根据 SearcherBean 的 Class 和 请求参数，自动检索，数据以 Map<String, Object> 对象呈现
 * 
 */
public class DefaultMapSearcher extends DefaultSearcher implements MapSearcher {

	@Override
	@SuppressWarnings("unchecked")
	public <T> SearchResult<Map<String, Object>> search(Class<T> beanClass, Map<String, Object> paraMap) {
		return (SearchResult<Map<String, Object>>) super.search(beanClass, paraMap);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> SearchResult<Map<String, Object>> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields) {
		return (SearchResult<Map<String, Object>>) super.search(beanClass, paraMap, summaryFields);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Map<String, Object> searchFirst(Class<T> beanClass, Map<String, Object> paraMap) {
		return (Map<String, Object>) super.searchFirst(beanClass, paraMap);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<Map<String, Object>> searchList(Class<T> beanClass, Map<String, Object> paraMap) {
		return (List<Map<String, Object>>) super.searchList(beanClass, paraMap);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<Map<String, Object>> searchAll(Class<T> beanClass, Map<String, Object> paraMap) {
		return (List<Map<String, Object>>) super.searchAll(beanClass, paraMap);
	}

}
