package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;
import com.ejlchina.searcher.implement.pagination.Pagination;
import com.ejlchina.searcher.param.SearchParam;
import com.ejlchina.searcher.util.StringUtils;
import com.ejlchina.searcher.virtual.VirtualParamProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 自动检索器 根据 Bean 的 Class 和请求参数，自动检索 Bean
 * 
 */
public class DefaultSearcher implements Searcher {
	
	private SearchParamResolver searchParamResolver;

	private SearchSqlResolver searchSqlResolver;

	private SearchSqlExecutor searchSqlExecutor;

	private VirtualParamProcessor virtualParamProcessor;

	private final Map<Class<?>, SearchBeanMap> cache = new ConcurrentHashMap<>();

	@Override
	public <T> SearchResult<?> search(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, null, true, true, false);
	}

	@Override
	public <T> SearchResult<?> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields) {
		return search(beanClass, paraMap, summaryFields, true, true, false);
	}

	@Override
	public <T> Object searchFirst(Class<T> beanClass, Map<String, Object> paraMap) {
		paraMap.put(getPagination().getMaxParamName(), "1");
		List<?> list = search(beanClass, paraMap, null, false, true, false).getDataList();
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public <T> List<?> searchList(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, null, false, true, false).getDataList();
	}

	@Override
	public <T> List<?> searchAll(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, null, false, true, true).getDataList();
	}

	@Override
	public <T> Number searchCount(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, null, true, false, true).getTotalCount();
	}

	@Override
	public <T> Number searchSum(Class<T> beanClass, Map<String, Object> paraMap, String field) {
		Number[] results = searchSum(beanClass, paraMap, new String[] { field });
		if (results != null && results.length > 0) {
			return results[0];
		}
		return null;
	}

	@Override
	public <T> Number[] searchSum(Class<T> beanClass, Map<String, Object> paraMap, String[] fields) {
		if (fields == null || fields.length == 0) {
			throw new SearcherException("检索该 Bean【" + beanClass.getName() 
			+ "】的统计信息时，必须要指定需要统计的属性！");
		}
		return search(beanClass, paraMap, fields, false, false, true).getSummaries();
	}
	
	/// 私有方法

	protected <T> SearchResult<?> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields,
				boolean shouldQueryTotal, boolean shouldQueryList, boolean needNotLimit) {
		SearchBeanMap beanMap = resolveSearchBeanMap(beanClass);
		List<String> fieldList = beanMap.getFieldList();
		SearchParam searchParam = searchParamResolver.resolve(fieldList, paraMap);
		searchParam.setSummaryFields(summaryFields);
		searchParam.setShouldQueryTotal(shouldQueryTotal);
		searchParam.setShouldQueryList(shouldQueryList);
		if (needNotLimit) {
			searchParam.setMax(null);
		}
		SearchSql searchSql = searchSqlResolver.resolve(beanMap, searchParam);
		searchSql.setShouldQueryCluster(shouldQueryTotal || (summaryFields != null && summaryFields.length > 0));
		searchSql.setShouldQueryList(shouldQueryList);
		return searchSqlExecutor.execute(searchSql);
	}

	protected SearchBeanMap resolveSearchBeanMap(Class<?> beanClass) {
		SearchBeanMap beanMap = cache.get(beanClass);
		if (beanMap != null) {
			return beanMap;
		}
		SearchBean searchBean = beanClass.getAnnotation(SearchBean.class);
		if (searchBean == null) {
			throw new SearcherException("The class [" + beanClass.getName()
					+ "] is not a valid SearchBean, please check whether the class is annotated correctly by @SearchBean");
		}
		synchronized (cache) {
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
			SearchBeanMap newBeanMap = virtualParamProcessor.process(searchBeanMap);
			addSearchBeanMap(beanClass, newBeanMap);
			return newBeanMap;
		}
	}

	protected <T> void addSearchBeanMap(Class<T> beanClass, SearchBeanMap searchBeanMap) {
		SearchResultConvertInfo<T> convertInfo = new SearchResultConvertInfo<>(beanClass);
		convertInfo.setFieldDbAliasEntrySet(searchBeanMap.getFieldDbAliasMap().entrySet());
		convertInfo.setFieldGetMethodMap(searchBeanMap.getFieldGetMethodMap());
		convertInfo.setFieldTypeMap(searchBeanMap.getFieldTypeMap());
		searchBeanMap.setConvertInfo(convertInfo);
		cache.put(beanClass, searchBeanMap);
	}

	public Pagination getPagination() {
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

	public VirtualParamProcessor getVirtualParamProcessor() {
		return virtualParamProcessor;
	}

	public void setVirtualParamProcessor(VirtualParamProcessor virtualParamProcessor) {
		this.virtualParamProcessor = virtualParamProcessor;
	}

}
