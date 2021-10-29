package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.param.SearchParam;

import java.util.List;
import java.util.Map;

/***
 * @author Troy.Zhou @ 2021-10-29
 * 
 * 自动检索器 根据 SearcherBean 的 Class 和 请求参数，自动检索，数据以 Map<String, Object> 对象呈现
 * 
 */
public class DefaultBeanSearcher extends DefaultSearcher implements BeanSearcher {

	private SearchResultResolver searchResultResolver;

	@Override
	public <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, null, true, true, false);
	}

	@Override
	public <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields) {
		return search(beanClass, paraMap, summaryFields, true, true, false);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T searchFirst(Class<T> beanClass, Map<String, Object> paraMap) {
		return (T) super.searchFirst(beanClass, paraMap);
	}

	@Override
	public <T> List<T> searchList(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, null, false, true, false).getDataList();
	}

	@Override
	public <T> List<T> searchAll(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, null, false, true, true).getDataList();
	}

	protected <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields,
														   boolean shouldQueryTotal, boolean shouldQueryList, boolean needNotLimit) {
		@SuppressWarnings("unchecked")
		SearchResult<Map<String, Object>> result = (SearchResult<Map<String, Object>>) super.search(beanClass, paraMap, summaryFields,
				shouldQueryTotal, shouldQueryList, needNotLimit);
		@SuppressWarnings("unchecked")
		SearchResultConvertInfo<T> convertInfo = (SearchResultConvertInfo<T>) resolveSearchBeanMap(beanClass).getConvertInfo();
		return searchResultResolver.resolve(convertInfo, result);
	}

	public SearchResultResolver getSearchResultResolver() {
		return searchResultResolver;
	}

	public void setSearchResultResolver(SearchResultResolver searchResultResolver) {
		this.searchResultResolver = searchResultResolver;
	}

}
