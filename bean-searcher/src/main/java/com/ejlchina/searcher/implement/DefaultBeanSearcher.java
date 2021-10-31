package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;

import java.sql.ResultSet;
import java.sql.SQLException;
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

	private BeanReflector beanReflector;

	@Override
	public <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, null, true, true, false);
	}

	@Override
	public <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields) {
		return search(beanClass, paraMap, summaryFields, true, true, false);
	}

	@Override
	public <T> T searchFirst(Class<T> beanClass, Map<String, Object> paraMap) {
		paraMap.put(getPagination().getSizeName(), "1");
		List<T> list = search(beanClass, paraMap, null, false, true, false).getDataList();
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public <T> List<T> searchList(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, null, false, true, false).getDataList();
	}

	@Override
	public <T> List<T> searchAll(Class<T> beanClass, Map<String, Object> paraMap) {
		return search(beanClass, paraMap, null, false, true, true).getDataList();
	}


	protected <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields,
								   boolean shouldQueryTotal, boolean shouldQueryList, boolean needNotLimit) {
		SqlResult<T> sqlResult = doSearch(beanClass, paraMap, summaryFields, shouldQueryTotal, shouldQueryList, needNotLimit);
		ResultSet dataListResult = sqlResult.getDataListResult();
		ResultSet clusterResult = sqlResult.getClusterResult();
		try {
			SearchResult<T> result;
			if (dataListResult != null) {
				Metadata<T> metadata = sqlResult.getSearchSql().getMetadata();
				List<T> beanList = beanReflector.reflect(metadata, dataListResult);
				result = new SearchResult<>(beanList);
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
			throw new SearchException("A exception occurred when collect sql result!", e);
		} finally {
			sqlResult.closeResultSet();
		}
	}

	public BeanReflector getBeanReflector() {
		return beanReflector;
	}

	public void setBeanReflector(BeanReflector beanReflector) {
		this.beanReflector = Objects.requireNonNull(beanReflector);
	}

}
