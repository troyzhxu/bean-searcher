package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.param.FetchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * 自动检索器 根据 Bean 的 Class 和请求参数，自动检索 Bean
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.0.0
 */
public abstract class AbstractSearcher implements Searcher {

	protected static final Logger log = LoggerFactory.getLogger(AbstractSearcher.class);

	private SqlExecutor sqlExecutor;

	private ParamResolver paramResolver = new DefaultParamResolver();

	private SqlResolver sqlResolver = new DefaultSqlResolver();

	private MetaResolver metaResolver = new DefaultMetaResolver();

	private List<SqlInterceptor> interceptors = new ArrayList<>();

	private List<ResultFilter> resultFilters = new ArrayList<>();

	public AbstractSearcher() {
	}

	public AbstractSearcher(SqlExecutor sqlExecutor) {
		this.sqlExecutor = sqlExecutor;
	}

	@Override
	public <T> Number searchCount(Class<T> beanClass, Map<String, Object> paraMap) {
		try (SqlResult<T> result = doSearch(beanClass, paraMap, new FetchType(FetchType.ONLY_TOTAL))) {
			return getCountFromSqlResult(result);
		} catch (SQLException e) {
			throw new SearchException("A exception occurred when collect sql result!", e);
		}
	}

	@Override
	public <T> Number searchSum(Class<T> beanClass, Map<String, Object> paraMap, String field) {
		String[] fields = { Objects.requireNonNull(field) };
		Number[] results = searchSum(beanClass, paraMap, fields);
		return results != null && results.length > 0 ? results[0] : 0L;
	}

	@Override
	public <T> Number[] searchSum(Class<T> beanClass, Map<String, Object> paraMap, String[] fields) {
		if (fields == null || fields.length == 0) {
			throw new SearchException("检索该 Bean【" + beanClass.getName() + "】的统计信息时，必须要指定需要统计的属性！");
		}
		try (SqlResult<T> result = doSearch(beanClass, paraMap, new FetchType(FetchType.ONLY_SUMMARY, fields))) {
			return getSummaryFromSqlResult(result);
		} catch (SQLException e) {
			throw new SearchException("A exception occurred when collect sql result!", e);
		}
	}

	protected Number getCountFromSqlResult(SqlResult<?> sqlResult) throws SQLException {
		Number count = (Number) sqlResult.getClusterResult().get(sqlResult.getSearchSql().getCountAlias());
		return count != null ? count : 0L;
	}

	protected Number[] getSummaryFromSqlResult(SqlResult<?> sqlResult) throws SQLException {
		List<String> summaryAliases = sqlResult.getSearchSql().getSummaryAliases();
		if (summaryAliases.isEmpty()) {
			return SearchResult.EMPTY_SUMMARIES;
		}
		SqlResult.Result clusterResult = sqlResult.getClusterResult();
		Number[] summaries = new Number[summaryAliases.size()];
		for (int i = 0; i < summaries.length; i++) {
			String summaryAlias = summaryAliases.get(i);
			Number sum = (Number) clusterResult.get(summaryAlias);
			summaries[i] = sum != null ? sum : 0L;
		}
		return summaries;
	}

	protected <T> SqlResult<T> doSearch(Class<T> beanClass, Map<String, Object> paraMap, FetchType fetchType) {
		if (sqlExecutor == null) {
			throw new SearchException("you must set a sqlExecutor before search.");
		}
		BeanMeta<T> beanMeta = metaResolver.resolve(beanClass);
		SearchParam searchParam;
		try {
			searchParam = paramResolver.resolve(beanMeta, fetchType, paraMap);
		} catch (IllegalArgumentException e) {
			log.warn("检测到非法检索参数 [{}] 本次检索直接返回空数据！", e.getMessage());
			// 参数非法的情况下，直接返回空数据
			return emptyResult(beanMeta, fetchType);
		}
		SearchSql<T> searchSql = sqlResolver.resolve(beanMeta, searchParam);
		return sqlExecutor.execute(intercept(searchSql, paraMap, fetchType));
	}

	private <T> SqlResult<T> emptyResult(BeanMeta<T> beanMeta, FetchType fetchType) {
		SearchSql<T> searchSql = new SearchSql<>(beanMeta, Collections.emptyList());
		for (String summaryField : fetchType.getSummaryFields()) {
			searchSql.addSummaryAlias(summaryField);
		}
		return new SqlResult<>(searchSql, SqlResult.ResultSet.EMPTY, col -> null);
	}

	protected <T> SearchSql<T> intercept(SearchSql<T> searchSql, Map<String, Object> paraMap, FetchType fetchType) {
		for (SqlInterceptor interceptor : interceptors) {
			searchSql = interceptor.intercept(searchSql, paraMap, fetchType);
		}
		return searchSql;
	}

	public ParamResolver getParamResolver() {
		return paramResolver;
	}

	public void setParamResolver(ParamResolver paramResolver) {
		this.paramResolver = Objects.requireNonNull(paramResolver);
	}

	public SqlResolver getSqlResolver() {
		return sqlResolver;
	}

	public void setSqlResolver(SqlResolver sqlResolver) {
		this.sqlResolver = Objects.requireNonNull(sqlResolver);
	}

	public SqlExecutor getSqlExecutor() {
		return sqlExecutor;
	}

	public void setSqlExecutor(SqlExecutor sqlExecutor) {
		this.sqlExecutor = Objects.requireNonNull(sqlExecutor);
	}

	public MetaResolver getMetaResolver() {
		return metaResolver;
	}

	public void setMetaResolver(MetaResolver metaResolver) {
		this.metaResolver = Objects.requireNonNull(metaResolver);
	}

	public List<SqlInterceptor> getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(List<SqlInterceptor> interceptors) {
		this.interceptors = Objects.requireNonNull(interceptors);
	}

	public List<ResultFilter> getResultFilters() {
		return resultFilters;
	}

	public void setResultFilters(List<ResultFilter> resultFilters) {
		this.resultFilters = Objects.requireNonNull(resultFilters);
	}

	public void addResultFilter(ResultFilter resultFilter) {
		if (resultFilter != null) {
			this.resultFilters.add(resultFilter);
		}
	}

}
