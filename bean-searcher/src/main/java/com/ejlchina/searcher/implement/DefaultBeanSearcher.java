package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.bean.BeanAware;
import com.ejlchina.searcher.bean.ParamAware;
import com.ejlchina.searcher.param.FetchType;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/***
 * 自动检索器 根据 SearcherBean 的 Class 和 请求参数，自动检索，数据以 Bean 对象呈现
 * @author Troy.Zhou @ 2021-10-29
 * @since v3.0.0
 */
public class DefaultBeanSearcher extends AbstractSearcher implements BeanSearcher {

	private BeanReflector beanReflector = new DefaultBeanReflector();

	public DefaultBeanSearcher() {
	}

	public DefaultBeanSearcher(SqlExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	@Override
	public <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, new FetchType(FetchType.DEFAULT));
	}

	@Override
	public <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields) {
		return search(beanClass, paraMap, new FetchType(FetchType.DEFAULT, summaryFields));
	}

	@Override
	public <T> T searchFirst(Class<T> beanClass, Map<String, Object> paraMap) {
		FetchType fetchType = new FetchType(FetchType.LIST_FIRST);
		List<T> list = search(beanClass, paraMap, fetchType).getDataList();
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public <T> List<T> searchList(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, new FetchType(FetchType.LIST_ONLY)).getDataList();
	}

	@Override
	public <T> List<T> searchAll(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, new FetchType(FetchType.LIST_ALL)).getDataList();
	}

	protected <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap, FetchType fetchType) {
		try (SqlResult<T> sqlResult = doSearch(beanClass, paraMap, fetchType)) {
			SearchSql<T> searchSql = sqlResult.getSearchSql();
			Number totalCount = 0L;
			Number[] summaries = SearchResult.EMPTY_SUMMARIES;
			if (searchSql.isShouldQueryCluster()) {
				totalCount = getCountFromSqlResult(sqlResult);
				summaries = getSummaryFromSqlResult(sqlResult);
			}
			SearchResult<T> result = new SearchResult<>(totalCount, summaries);
			BeanMeta<T> beanMeta = searchSql.getBeanMeta();
			SqlResult.ResultSet listResult = sqlResult.getListResult();
			if (listResult != null) {
				List<String> fetchFields = searchSql.getFetchFields();
				collectList(result, listResult, beanMeta, fetchFields, paraMap);
			}
			return doFilter(result, beanMeta, paraMap, fetchType);
		} catch (SQLException e) {
			throw new SearchException("A exception occurred when collecting sql result!", e);
		}
	}

	protected <T> void collectList(SearchResult<T> result, SqlResult.ResultSet listResult,
								   BeanMeta<T> beanMeta, List<String> fetchFields,
								   Map<String, Object> paraMap) throws SQLException {
		while (listResult.next()) {
			T bean = beanReflector.reflect(beanMeta, fetchFields, dbAlias -> {
				try {
					return listResult.get(dbAlias);
				} catch (SQLException e) {
					throw new SearchException("A exception occurred when collecting sql result!", e);
				}
			});
			if (bean instanceof BeanAware) {
				((BeanAware) bean).afterAssembly();
			}
			if (bean instanceof ParamAware) {
				((ParamAware) bean).afterAssembly(paraMap);
			}
			result.addData(bean);
		}
	}

	protected <T> SearchResult<T> doFilter(SearchResult<T> result, BeanMeta<T> beanMeta, Map<String, Object> paraMap,
										   FetchType fetchType) {
		for (ResultFilter filter: getResultFilters()) {
			result = filter.doBeanFilter(result, beanMeta, paraMap, fetchType);
		}
		return result;
	}

	public BeanReflector getBeanReflector() {
		return beanReflector;
	}

	public void setBeanReflector(BeanReflector beanReflector) {
		this.beanReflector = Objects.requireNonNull(beanReflector);
	}

}
