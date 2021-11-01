package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.param.FetchType;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/***
 * @author Troy.Zhou @ 2021-10-29
 * 
 * 自动检索器 根据 SearcherBean 的 Class 和 请求参数，自动检索，数据以 Map<String, Object> 对象呈现
 * 
 */
public class DefaultMapSearcher extends AbstractSearcher implements MapSearcher {

	@Override
	public <T> SearchResult<Map<String, Object>> search(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, new FetchType(FetchType.ALL));
	}

	@Override
	public <T> SearchResult<Map<String, Object>> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields) {
		return search(beanClass, paraMap, new FetchType(FetchType.ALL, summaryFields));
	}

	@Override
	public <T> Map<String, Object> searchFirst(Class<T> beanClass, Map<String, Object> paraMap) {
		FetchType fetchType = new FetchType(FetchType.LIST_FIRST);
		List<Map<String, Object>> list = search(beanClass, paraMap, fetchType).getDataList();
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public <T> List<Map<String, Object>> searchList(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, new FetchType(FetchType.LIST_ONLY)).getDataList();
	}

	@Override
	public <T> List<Map<String, Object>> searchAll(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, new FetchType(FetchType.LIST_ALL)).getDataList();
	}

	protected <T> SearchResult<Map<String, Object>> search(Class<T> beanClass, Map<String, Object> paraMap, FetchType fetchType) {
		SqlResult<T> sqlResult = doSearch(beanClass, paraMap, fetchType);
		ResultSet dataListResult = sqlResult.getDataListResult();
		ResultSet clusterResult = sqlResult.getClusterResult();
		try {
			SearchResult<Map<String, Object>> result = new SearchResult<>();
			if (dataListResult != null) {
				Set<Map.Entry<String, String>> fieldDbAliasEntrySet = sqlResult.getSearchSql().getMetadata().getFieldDbAliasEntrySet();
				while (dataListResult.next()) {
					Map<String, Object> dataMap = new HashMap<>();
					for (Map.Entry<String, String> entry : fieldDbAliasEntrySet) {
						dataMap.put(entry.getKey(), dataListResult.getObject(entry.getValue()));
					}
					result.addData(dataMap);
				}
			}
			if (clusterResult != null) {
				if (clusterResult.next()) {
					result.setTotalCount(getCountFromSqlResult(sqlResult));
					result.setSummaries(getSummaryFromSqlResult(sqlResult));
				}
			}
			return result;
		} catch (SQLException e) {
			throw new SearchException("A exception occurred when collecting sql result!", e);
		} finally {
			sqlResult.closeResultSet();
		}
	}

}
