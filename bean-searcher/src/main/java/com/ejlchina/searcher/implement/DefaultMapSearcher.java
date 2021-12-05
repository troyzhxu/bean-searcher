package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.FieldConvertor.MFieldConvertor;
import com.ejlchina.searcher.param.FetchType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/***
 * 自动检索器 根据 SearcherBean 的 Class 和 请求参数，自动检索，数据以 Map 对象呈现
 * @author Troy.Zhou @ 2021-10-29
 * @since v3.0.0
 */
public class DefaultMapSearcher extends AbstractSearcher implements MapSearcher {

	private List<MFieldConvertor> convertors = new ArrayList<>();

	public DefaultMapSearcher() {
	}

	public DefaultMapSearcher(SqlExecutor sqlExecutor) {
		super(sqlExecutor);
	}

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
		try (SqlResult<T> sqlResult = doSearch(beanClass, paraMap, fetchType)) {
			ResultSet listResult = sqlResult.getListResult();
			ResultSet clusterResult = sqlResult.getAlreadyClusterResult();
			SearchResult<Map<String, Object>> result = new SearchResult<>();
			if (listResult != null) {
				SearchSql<T> searchSql = sqlResult.getSearchSql();
				BeanMeta<T> beanMeta = searchSql.getBeanMeta();
				List<String> fetchFields = searchSql.getFetchFields();
				while (listResult.next()) {
					Map<String, Object> dataMap = new HashMap<>();
					for (String field : fetchFields) {
						FieldMeta meta = beanMeta.requireFieldMeta(field);
						Object value = listResult.getObject(meta.getDbAlias());
						dataMap.put(meta.getName(), convert(meta, value));
					}
					result.addData(dataMap);
				}
			}
			if (clusterResult != null) {
				result.setTotalCount(getCountFromSqlResult(sqlResult));
				result.setSummaries(getSummaryFromSqlResult(sqlResult));
			}
			return result;
		} catch (SQLException e) {
			throw new SearchException("A exception occurred when collecting sql result!", e);
		}
	}

	protected Object convert(FieldMeta meta, Object value) {
		if (value != null && convertors.size() > 0) {
			Class<?> valueType = value.getClass();
			for (FieldConvertor convertor : convertors) {
				if (convertor.supports(meta, valueType)) {
					return convertor.convert(meta, value);
				}
			}
		}
		return value;
	}


	public List<MFieldConvertor> getConvertors() {
		return convertors;
	}

	public void setConvertors(List<MFieldConvertor> convertors) {
		this.convertors = Objects.requireNonNull(convertors);
	}

	public void addConvertor(MFieldConvertor convertor) {
		if (convertor != null) {
			convertors.add(convertor);
		}
	}

}
