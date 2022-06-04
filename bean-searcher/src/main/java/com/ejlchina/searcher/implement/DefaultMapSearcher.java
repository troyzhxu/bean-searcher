package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.FieldConvertor.MFieldConvertor;
import com.ejlchina.searcher.param.FetchType;

import java.sql.SQLException;
import java.util.*;

/**
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
		return search(beanClass, paraMap, new FetchType(FetchType.DEFAULT));
	}

	@Override
	public <T> SearchResult<Map<String, Object>> search(Class<T> beanClass, Map<String, Object> paraMap,
														String[] summaryFields) {
		return search(beanClass, paraMap, new FetchType(FetchType.DEFAULT, summaryFields));
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
			SearchSql<T> searchSql = sqlResult.getSearchSql();
			Number totalCount = 0L;
			Number[] summaries = SearchResult.EMPTY_SUMMARIES;
			if (searchSql.isShouldQueryCluster()) {
				totalCount = getCountFromSqlResult(sqlResult);
				summaries = getSummaryFromSqlResult(sqlResult);
			}
			SearchResult<Map<String, Object>> result = new SearchResult<>(totalCount, summaries);
			BeanMeta<T> beanMeta = searchSql.getBeanMeta();
			SqlResult.ResultSet listResult = sqlResult.getListResult();
			if (listResult != null) {
				List<String> fetchFields = searchSql.getFetchFields();
				while (listResult.next()) {
					Map<String, Object> data = new HashMap<>(fetchFields.size());
					for (String field : fetchFields) {
						FieldMeta meta = beanMeta.requireFieldMeta(field);
						Object value = listResult.get(meta.getDbAlias());
						data.put(meta.getName(), convert(meta, value));
					}
					result.addData(data);
				}
			}
			return doFilter(result, beanMeta, paraMap, fetchType);
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

	protected <T> SearchResult<Map<String, Object>> doFilter(SearchResult<Map<String, Object>> result, BeanMeta<T> beanMeta,
															 Map<String, Object> paraMap, FetchType fetchType) {
		for (ResultFilter filter: getResultFilters()) {
			result = filter.doMapFilter(result, beanMeta, paraMap, fetchType);
		}
		return result;
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
