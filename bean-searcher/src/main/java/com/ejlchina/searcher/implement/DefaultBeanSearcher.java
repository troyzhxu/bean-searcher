package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.bean.BeanAware;
import com.ejlchina.searcher.bean.BeanParaAware;
import com.ejlchina.searcher.param.FetchType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/***
 * @author Troy.Zhou @ 2021-10-29
 * 
 * 自动检索器 根据 SearcherBean 的 Class 和 请求参数，自动检索，数据以 Map<String, Object> 对象呈现
 * 
 */
public class DefaultBeanSearcher extends AbstractSearcher implements BeanSearcher {

	private BeanReflector beanReflector = new DefaultBeanReflector();

	@Override
	public <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, new FetchType(FetchType.ALL));
	}

	@Override
	public <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields) {
		return search(beanClass, paraMap, new FetchType(FetchType.ALL, summaryFields));
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
		SqlResult<T> sqlResult = doSearch(beanClass, paraMap, fetchType);
		ResultSet listResult = sqlResult.getListResult();
		ResultSet clusterResult = sqlResult.getClusterResult();
		try {
			SearchResult<T> result;
			if (listResult != null) {
				SearchSql<T> searchSql = sqlResult.getSearchSql();
				BeanMeta<T> beanMeta = searchSql.getBeanMeta();
				List<String> fetchFields = searchSql.getFetchFields();
				result = new SearchResult<>(toBeanList(listResult, beanMeta, fetchFields, paraMap));
			} else {
				result = new SearchResult<>();
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

	protected <T> List<T> toBeanList(ResultSet listResult, BeanMeta<T> beanMeta, List<String> fetchFields, Map<String, Object> paraMap) throws SQLException {
		List<T> dataList = new ArrayList<>();
		while (listResult.next()) {
			T bean = beanReflector.reflect(beanMeta, fetchFields, dbAlias -> {
				try {
					return listResult.getObject(dbAlias);
				} catch (SQLException e) {
					throw new SearchException("A exception occurred when collecting sql result!", e);
				}
			});
			if (bean instanceof BeanAware) {
				((BeanAware) bean).afterAssembly();
			}
			if (bean instanceof BeanParaAware) {
				((BeanParaAware) bean).afterAssembly(paraMap);
			}
			dataList.add(bean);
		}
		return dataList;
	}

	public BeanReflector getBeanReflector() {
		return beanReflector;
	}

	public void setBeanReflector(BeanReflector beanReflector) {
		this.beanReflector = Objects.requireNonNull(beanReflector);
	}

}
