package com.ejl.searcher.implement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ejl.searcher.SearchParamResolver;
import com.ejl.searcher.SearchResult;
import com.ejl.searcher.SearchResultConvertInfo;
import com.ejl.searcher.SearchResultResolver;
import com.ejl.searcher.SearchSql;
import com.ejl.searcher.SearchSqlExecutor;
import com.ejl.searcher.SearchSqlResolver;
import com.ejl.searcher.SearchTmpResult;
import com.ejl.searcher.Searcher;
import com.ejl.searcher.beanmap.SearchBeanMap;
import com.ejl.searcher.beanmap.SearchBeanMapCache;
import com.ejl.searcher.param.SearchParam;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 自动检索器 根据 Bean 的 Class 和请求参数，自动检索 Bean
 * 
 */
public class MainSearcher implements Searcher {

	private SearchParamResolver searchParamResolver;

	private SearchSqlResolver searchSqlResolver;

	private SearchSqlExecutor searchSqlExecutor;

	private SearchResultResolver searchResultResolver;

	/**
	 * 前缀分隔符长度
	 */
	private int prifexSeparatorLength = 1;

	
	@Override
	public <T> SearchResult<T> search(Class<T> beanClass, Map<String, String> paraMap, String prefix) {
		return search(beanClass, propcessParaMapWhithPrefix(paraMap, prefix));
	}
	
	@Override
	public <T> SearchResult<T> search(Class<T> beanClass, Map<String, String> paraMap) {
		return search(beanClass, paraMap, true, true);
	}
	
	@Override
	public <T> T searchFirst(Class<T> beanClass, Map<String, String> paraMap, String prefix) {
		return searchFirst(beanClass, propcessParaMapWhithPrefix(paraMap, prefix));
	}

	@Override
	public <T> T searchFirst(Class<T> beanClass, Map<String, String> paraMap) {
		String maxParamName = searchParamResolver.getMaxParamName();
		paraMap.put(maxParamName, "1");
		List<T> list = search(beanClass, paraMap, false, true).getDataList();
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public <T> List<T> searchList(Class<T> beanClass, Map<String, String> paraMap, String prefix) {
		return searchList(beanClass, propcessParaMapWhithPrefix(paraMap, prefix));
	}

	@Override
	public <T> List<T> searchList(Class<T> beanClass, Map<String, String> paraMap) {
		return search(beanClass, paraMap, false, true).getDataList();
	}
	
	@Override
	public <T> Number searchCount(Class<T> beanClass, Map<String, String> paraMap, String prefix) {
		return searchCount(beanClass, propcessParaMapWhithPrefix(paraMap, prefix));
	}

	@Override
	public <T> Number searchCount(Class<T> beanClass, Map<String, String> paraMap) {
		return search(beanClass, paraMap, true, false).getTotalCount();
	}

	/// 私有方法

	private Map<String, String> propcessParaMapWhithPrefix(Map<String, String> paraMap, String prefix) {
		Map<String, String> newParaMap = null;
		if (prefix != null) {
			newParaMap = new HashMap<>();
			for (Entry<String, String> entry : paraMap.entrySet()) {
				String key = entry.getKey();
				if (key != null && key.length() > prefix.length() + prifexSeparatorLength 
						&& key.startsWith(prefix)) {
					newParaMap.put(key.substring(prefix.length() + prifexSeparatorLength), entry.getValue());
				}
			}
		} else {
			newParaMap = paraMap;
		}
		return newParaMap;
	}

	private <T> SearchResult<T> search(Class<T> beanClass, Map<String, String> paraMap, boolean shouldQueryTotal, boolean shouldQueryList) {
		SearchBeanMap searchBeanMap = SearchBeanMapCache.sharedCache().getSearchBeanMap(beanClass);
		if (searchBeanMap == null) {
			throw new RuntimeException("该 Bean【" + beanClass.getName() + "】不可以被检索器检索，请检查该Class是否被正确注解 或 检索器是否正确启动！");
		}
		List<String> fieldList = searchBeanMap.getFieldList();
		SearchParam searchParam = searchParamResolver.resolve(fieldList, paraMap);
		SearchSql searchSql = searchSqlResolver.resolve(searchBeanMap, searchParam);
		searchSql.setShouldQueryTotal(shouldQueryTotal);
		searchSql.setShouldQueryList(shouldQueryList);
		SearchTmpResult searchTmpResult = searchSqlExecutor.execute(searchSql);
		SearchResultConvertInfo<T> convertInfo = new SearchResultConvertInfo<T>(beanClass);
		convertInfo.setFieldDbAliasEntrySet(searchBeanMap.getFieldDbAliasMap().entrySet());
		convertInfo.setFieldGetMethodMap(searchBeanMap.getFieldGetMethodMap());
		convertInfo.setFieldTypeMap(searchBeanMap.getFieldTypeMap());
		return searchResultResolver.resolve(convertInfo, searchTmpResult);
	}
	
	public SearchParamResolver getSearchParamResolver() {
		return searchParamResolver;
	}

	public void setSearchParamResolver(SearchParamResolver searchParamResolver) {
		this.searchParamResolver = searchParamResolver;
	}

	public SearchSqlResolver getSearchSqlResolver() {
		return searchSqlResolver;
	}

	public void setSearchSqlResolver(SearchSqlResolver searchSqlResolver) {
		this.searchSqlResolver = searchSqlResolver;
	}

	public SearchSqlExecutor getSearchSqlExecutor() {
		return searchSqlExecutor;
	}

	public void setSearchSqlExecutor(SearchSqlExecutor searchSqlExecutor) {
		this.searchSqlExecutor = searchSqlExecutor;
	}

	public SearchResultResolver getSearchResultResolver() {
		return searchResultResolver;
	}

	public void setSearchResultResolver(SearchResultResolver searchResultResolver) {
		this.searchResultResolver = searchResultResolver;
	}

	public int getPrifexSeparatorLength() {
		return prifexSeparatorLength;
	}

	public void setPrifexSeparatorLength(int prifexSeparatorLength) {
		this.prifexSeparatorLength = prifexSeparatorLength;
	}

}
