package cn.zhxu.bs.implement;

import cn.zhxu.bs.*;
import cn.zhxu.bs.FieldConvertor.MFieldConvertor;
import cn.zhxu.bs.param.FetchType;
import cn.zhxu.bs.util.FieldFns;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

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
	public <T> SearchResult<Map<String, Object>> search(Class<T> beanClass) {
		return search(beanClass, null, new FetchType(FetchType.DEFAULT));
	}

	@Override
	public <T> SearchResult<Map<String, Object>> search(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, new FetchType(FetchType.DEFAULT));
	}

	@Override
	public <T> SearchResult<Map<String, Object>> search(Class<T> beanClass, String summaryField) {
		return search(beanClass, null, summaryField);
	}

	@Override
	public <T> SearchResult<Map<String, Object>> search(Class<T> beanClass, Map<String, Object> paraMap, String summaryField) {
		if (summaryField != null) {
			return search(beanClass, paraMap, new String[] { summaryField });
		}
		return search(beanClass, paraMap);
	}

	@Override
	public <T> SearchResult<Map<String, Object>> search(Class<T> beanClass, FieldFns.FieldFn<T, ?> summaryField) {
		return search(beanClass, null, summaryField);
	}

	@Override
	public <T> SearchResult<Map<String, Object>> search(Class<T> beanClass, Map<String, Object> paraMap, FieldFns.FieldFn<T, ?> summaryField) {
		if (summaryField != null) {
			return search(beanClass, paraMap, FieldFns.name(summaryField));
		}
		return search(beanClass, paraMap);
	}

	@Override
	public <T> SearchResult<Map<String, Object>> search(Class<T> beanClass, String[] summaryFields) {
		return search(beanClass, null, summaryFields);
	}

	@Override
	public <T> SearchResult<Map<String, Object>> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields) {
		if (summaryFields != null) {
			return search(beanClass, paraMap, new FetchType(FetchType.DEFAULT, summaryFields));
		}
		return search(beanClass, paraMap);
	}

	@Override
	public <T> Map<String, Object> searchFirst(Class<T> beanClass) {
		return searchFirst(beanClass, null);
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
	public <T> List<Map<String, Object>> searchList(Class<T> beanClass) {
		return searchList(beanClass, null);
	}

	@Override
	public <T> List<Map<String, Object>> searchList(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, new FetchType(FetchType.LIST_ONLY)).getDataList();
	}

	@Override
	public <T> List<Map<String, Object>> searchAll(Class<T> beanClass) {
		return searchAll(beanClass, null);
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
			SearchResult<Map<String, Object>> result = new SearchResult<>(totalCount, sqlResult.getPageSize(), summaries);
			BeanMeta<T> beanMeta = searchSql.getBeanMeta();
			SqlResult.ResultSet listResult = sqlResult.getListResult();
			if (listResult != null) {
				List<FieldMeta> fieldMetas = searchSql.getFetchFields().stream()
						.map(beanMeta::requireFieldMeta)
						.collect(Collectors.toList());
				List<Map<String, Object>> dataList = result.getDataList();
				while (listResult.next()) {
					Map<String, Object> data = new HashMap<>(fieldMetas.size());
					for (FieldMeta meta : fieldMetas) {
						Object value = listResult.get(meta.getDbAlias());
						data.put(meta.getName(), convert(meta, value));
					}
					dataList.add(data);
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
