package com.ejl.searcher.implement;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ejl.searcher.SearchParamResolver;
import com.ejl.searcher.implement.pagination.MaxOffsetPaginationResolver;
import com.ejl.searcher.implement.pagination.PaginationResolver;
import com.ejl.searcher.implement.parafilter.ParamFilter;
import com.ejl.searcher.param.FilterOperator;
import com.ejl.searcher.param.FilterParam;
import com.ejl.searcher.param.SearchParam;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 *         默认查询参数解析器
 * 
 */
public class MainSearchParamResolver implements SearchParamResolver {

	/**
	 * 默认最大条数
	 */
	private Integer defaultMax = 10;
	
	/**
	 * 排序字段参数名
	 */
	private String sortParamName = "sort";
	
	/**
	 * 排序方法字段参数名
	 */
	private String orderParamName = "order";
	
	/**
	 * 忽略大小写参数名后缀
	 */
	private String ignoreCaseParamNameSuffix = "_ic";

	/**
	 * 忽略大小写参数名后缀
	 */
	private String filterOperationParamNameSuffix = "_op";
	
	/**
	 * Boolean true 值参数后缀
	 */
	private String booleanTrueParamNameSuffix = "_true";
	
	/**
	 * Boolean false 值参数后缀
	 */
	private String booleanFalseParamNameSuffix = "_false";
	
	/**
	 * 双值参数后缀
	 */
	private String value2ParamNameSuffix = "_2";
	
	/**
	 * 参数名分割符
	 */
	private String paramNameSeparator = "_";
	
	
	private PaginationResolver paginationResolver = new MaxOffsetPaginationResolver();
	
	private ParamFilter[] paramFilters;
	
	/**
	 * @return 最大条数参数名
	 */
	public String getMaxParamName() {
		return paginationResolver.getMaxParamName();
	}
	

	@Override
	public SearchParam resolve(List<String> fieldList, Map<String, String> paraMap) {
		if (paramFilters != null) {
			for (ParamFilter paramFilter: paramFilters) {
				paraMap = paramFilter.doFilte(paraMap);
			}
		}
		SearchParam searchParam = new SearchParam(defaultMax);
		Set<Entry<String, String>> entrySet = paraMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key.equals(sortParamName)) {
				searchParam.setSort(value);
				continue;
			}
			if (key.equals(orderParamName)) {
				searchParam.setOrder(value);
				continue;
			}
			if (paginationResolver.resolve(searchParam, key, value)) {
				continue;
			}
			if (shouldSkipKey(fieldList, key)) {
				continue;
			}
			if (key.endsWith(booleanTrueParamNameSuffix)) {
				String field = key.substring(0, key.length() - booleanTrueParamNameSuffix.length());
				FilterParam filterParam = findFilterParam(searchParam, field);
				if (filterParam == null) {
					filterParam = new FilterParam();
					filterParam.setName(field);
					searchParam.addFilterParam(filterParam);
				}
				filterParam.setOperator(FilterOperator.Equal);
				filterParam.setValue("1");
			} else if (key.endsWith(booleanFalseParamNameSuffix)) {
				String field = key.substring(0, key.length() - booleanFalseParamNameSuffix.length());
				FilterParam filterParam = findFilterParam(searchParam, field);
				if (filterParam == null) {
					filterParam = new FilterParam();
					filterParam.setName(field);
					searchParam.addFilterParam(filterParam);
				}
				filterParam.setOperator(FilterOperator.Equal);
				filterParam.setValue("0");
			} else if (key.endsWith(ignoreCaseParamNameSuffix)) {
				String field = key.substring(0, key.length() - ignoreCaseParamNameSuffix.length());
				FilterParam filterParam = findFilterParam(searchParam, field);
				if (filterParam == null) {
					filterParam = new FilterParam();
					filterParam.setName(field);
					searchParam.addFilterParam(filterParam);
				}
				String upVal = value != null ? value.toUpperCase() : value;
				if ("0".equals(upVal) || "OFF".equals(upVal) || "FALSE".endsWith(upVal) || "N".endsWith(upVal)
						|| "NO".endsWith(upVal)) {
					filterParam.setIgnoreCase(false);
				} else {
					filterParam.setIgnoreCase(true);
				}
			} else if (key.endsWith(filterOperationParamNameSuffix)) {
				String field = key.substring(0, key.length() - filterOperationParamNameSuffix.length());
				FilterParam filterParam = findFilterParam(searchParam, field);
				if (filterParam == null) {
					filterParam = new FilterParam();
					filterParam.setName(field);
					searchParam.addFilterParam(filterParam);
				}
				FilterOperator operator = FilterOperator.from(value);
				filterParam.setOperator(operator);
			} else if (key.endsWith(value2ParamNameSuffix)) {
				String field = key.substring(0, key.length() - value2ParamNameSuffix.length());
				FilterParam filterParam = findFilterParam(searchParam, field);
				if (filterParam == null) {
					filterParam = new FilterParam();
					filterParam.setName(field);
					searchParam.addFilterParam(filterParam);
				}
				filterParam.setValue2(value);
			} else {
				FilterParam filterParam = findFilterParam(searchParam, key);
				if (filterParam == null) {
					filterParam = new FilterParam();
					filterParam.setName(key);
					searchParam.addFilterParam(filterParam);
				}
				filterParam.setValue(value);
				if (filterParam.getOperator() == null) {
					filterParam.setOperator(FilterOperator.Equal);
				}
			}
		}
		searchParam.removeUselessFilterParam();
		return searchParam;
	}

	private FilterParam findFilterParam(SearchParam searchParam, String field) {
		List<FilterParam> list = searchParam.getFilterParamList();
		for (FilterParam filterParam : list) {
			if (filterParam.getName().equals(field)) {
				return filterParam;
			}
		}
		return null;
	}

	private boolean shouldSkipKey(List<String> fieldList, String key) {
		boolean should = true;
		for (String field : fieldList) {
			if (key.equals(field) || key.startsWith(field + paramNameSeparator)) {
				should = false;
				break;
			}
		}
		return should;
	}

	public void setDefaultMax(Integer defaultMax) {
		this.defaultMax = defaultMax;
	}
	
	public void setSortParamName(String sortParamName) {
		this.sortParamName = sortParamName;
	}

	public void setOrderParamName(String orderParamName) {
		this.orderParamName = orderParamName;
	}

	public void setIgnoreCaseParamNameSuffix(String ignoreCaseParamNameSuffix) {
		this.ignoreCaseParamNameSuffix = ignoreCaseParamNameSuffix;
	}

	public void setFilterOperationParamNameSuffix(String filterOperationParamNameSuffix) {
		this.filterOperationParamNameSuffix = filterOperationParamNameSuffix;
	}

	public void setBooleanTrueParamNameSuffix(String booleanTrueParamNameSuffix) {
		this.booleanTrueParamNameSuffix = booleanTrueParamNameSuffix;
	}

	public void setBooleanFalseParamNameSuffix(String booleanFalseParamNameSuffix) {
		this.booleanFalseParamNameSuffix = booleanFalseParamNameSuffix;
	}

	public void setParamNameSeparator(String paramNameSeparator) {
		this.paramNameSeparator = paramNameSeparator;
	}

	public void setBetweenParamNameSuffix(String betweenParamNameSuffix) {
		this.value2ParamNameSuffix = betweenParamNameSuffix;
	}

	public void setPaginationResolver(PaginationResolver paginationResolver) {
		this.paginationResolver = paginationResolver;
	}

	public void setParamFilters(ParamFilter[] paramFilters) {
		this.paramFilters = paramFilters;
	}
	
	
}
