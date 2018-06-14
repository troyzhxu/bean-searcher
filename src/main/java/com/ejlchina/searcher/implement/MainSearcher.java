package com.ejlchina.searcher.implement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ejlchina.searcher.SearchParamResolver;
import com.ejlchina.searcher.SearchResult;
import com.ejlchina.searcher.SearchResultConvertInfo;
import com.ejlchina.searcher.SearchResultResolver;
import com.ejlchina.searcher.SearchSql;
import com.ejlchina.searcher.SearchSqlExecutor;
import com.ejlchina.searcher.SearchSqlResolver;
import com.ejlchina.searcher.SearchTmpResult;
import com.ejlchina.searcher.Searcher;
import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.beanmap.SearchBeanMap;
import com.ejlchina.searcher.beanmap.SearchBeanMapCache;
import com.ejlchina.searcher.implement.pagination.Pagination;
import com.ejlchina.searcher.param.SearchParam;
import com.ejlchina.searcher.util.StringUtils;

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
		return search(beanClass, paraMap, null, true, true, false);
	}
	
	@Override
	public <T> SearchResult<T> search(Class<T> beanClass, Map<String, String> paraMap, String[] summaryFields,
			String prefix) {
		return search(beanClass, propcessParaMapWhithPrefix(paraMap, prefix), summaryFields);
	}

	@Override
	public <T> SearchResult<T> search(Class<T> beanClass, Map<String, String> paraMap, String[] summaryFields) {
		return search(beanClass, paraMap, summaryFields, true, true, false);
	}
	
	@Override
	public <T> T searchFirst(Class<T> beanClass, Map<String, String> paraMap, String prefix) {
		return searchFirst(beanClass, propcessParaMapWhithPrefix(paraMap, prefix));
	}

	@Override
	public <T> T searchFirst(Class<T> beanClass, Map<String, String> paraMap) {
		paraMap.put(getPagination().getMaxParamName(), "1");
		List<T> list = search(beanClass, paraMap, null, false, true, false).getDataList();
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
		return search(beanClass, paraMap, null, false, true, false).getDataList();
	}
	
	@Override
	public <T> List<T> searchAll(Class<T> beanClass, Map<String, String> paraMap, String prefix) {
		return searchAll(beanClass, propcessParaMapWhithPrefix(paraMap, prefix));
	}

	@Override
	public <T> List<T> searchAll(Class<T> beanClass, Map<String, String> paraMap) {	
		return search(beanClass, paraMap, null, false, true, true).getDataList();
	}
	
	@Override
	public <T> Number searchCount(Class<T> beanClass, Map<String, String> paraMap, String prefix) {
		return searchCount(beanClass, propcessParaMapWhithPrefix(paraMap, prefix));
	}

	@Override
	public <T> Number searchCount(Class<T> beanClass, Map<String, String> paraMap) {
		return search(beanClass, paraMap, null, true, false, true).getTotalCount();
	}
	
	@Override
	public <T> Number searchSum(Class<T> beanClass, Map<String, String> paraMap, String field, String prefix) {
		return searchSum(beanClass, propcessParaMapWhithPrefix(paraMap, prefix), field);
	}

	@Override
	public <T> Number searchSum(Class<T> beanClass, Map<String, String> paraMap, String field) {
		Number[] results = searchSum(beanClass, paraMap, new String[] { field });
		if (results != null && results.length > 0) {
			return results[0];
		}
		return null;
	}
	
	@Override
	public <T> Number[] searchSum(Class<T> beanClass, Map<String, String> paraMap, String[] fields, String prefix) {
		return searchSum(beanClass, propcessParaMapWhithPrefix(paraMap, prefix), fields);
	}

	@Override
	public <T> Number[] searchSum(Class<T> beanClass, Map<String, String> paraMap, String[] fields) {
		if (fields == null || fields.length == 0) {
			throw new SearcherException("检索该 Bean【" + beanClass.getName() 
			+ "】的统计信息时，必须要指定需要统计的属性！");
		}
		return search(beanClass, paraMap, fields, false, false, true).getSummaries();
	}
	
	/// 私有方法

	private Map<String, String> propcessParaMapWhithPrefix(Map<String, String> paraMap, String prefix) {
		Map<String, String> newParaMap = null;
		if (!StringUtils.isBlank(prefix)) {
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

	private <T> SearchResult<T> search(Class<T> beanClass, Map<String, String> paraMap, String[] summaryFields,
				boolean shouldQueryTotal, boolean shouldQueryList, boolean needNotLimit) {
		SearchBeanMap searchBeanMap = SearchBeanMapCache.sharedCache().getSearchBeanMap(beanClass);
		if (searchBeanMap == null) {
			throw new SearcherException("该 Bean【" + beanClass.getName() 
					+ "】不可以被检索器检索，请检查该Class是否被正确注解 或 检索器是否正确启动！");
		}
		List<String> fieldList = searchBeanMap.getFieldList();
		SearchParam searchParam = searchParamResolver.resolve(fieldList, paraMap);
		searchParam.setSummaryFields(summaryFields);
		searchParam.setShouldQueryTotal(shouldQueryTotal);
		searchParam.setShouldQueryList(shouldQueryList);
		if (needNotLimit) {
			searchParam.setMax(null);
		}
		SearchSql searchSql = searchSqlResolver.resolve(searchBeanMap, searchParam);
		searchSql.setShouldQueryCluster(shouldQueryTotal || (summaryFields != null && summaryFields.length > 0));
		searchSql.setShouldQueryList(shouldQueryList);
		SearchTmpResult searchTmpResult = searchSqlExecutor.execute(searchSql);
		SearchResultConvertInfo<T> convertInfo = new SearchResultConvertInfo<T>(beanClass);
		convertInfo.setFieldDbAliasEntrySet(searchBeanMap.getFieldDbAliasMap().entrySet());
		convertInfo.setFieldGetMethodMap(searchBeanMap.getFieldGetMethodMap());
		convertInfo.setFieldTypeMap(searchBeanMap.getFieldTypeMap());
		SearchResult<T> result = searchResultResolver.resolve(convertInfo, searchTmpResult);
		return consummateSearchResult(searchParam, result);
	}

	private <T> SearchResult<T> consummateSearchResult(SearchParam searchParam, SearchResult<T> result) {
		Integer max = searchParam.getMax();
		Long offset = searchParam.getOffset();
		if (offset == null) {
			offset = 0L;
		}
		result.setMax(max);
		result.setOffset(offset);
		int startPage = getPagination().getStartPage();
		Number totalCount = result.getTotalCount();
		if (max != null && totalCount != null) {
			long maxLong = max.longValue();
			long totalLong = totalCount.longValue();
			long totalPage = totalLong / maxLong;
			if (totalPage * maxLong < totalLong) {
				totalPage = totalPage + 1;
			}
			result.setTotalPage(totalPage);
			result.setPage(startPage + offset / max);
		} else {
			result.setTotalPage(1);
			result.setPage(startPage);
		}
		return result;
	}

	private Pagination getPagination() {
		return searchParamResolver.getPagination();
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
