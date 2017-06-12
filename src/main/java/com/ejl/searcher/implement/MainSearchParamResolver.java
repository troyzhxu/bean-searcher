package com.ejl.searcher.implement;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ejl.searcher.SearchParamResolver;
import com.ejl.searcher.param.FilterOperator;
import com.ejl.searcher.param.FilterParam;
import com.ejl.searcher.param.SearchParam;

import java.util.Set;

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
	 * 最大条数字段参数名
	 */
	private String maxParamName = "max";
	
	/**
	 * 偏移条数字段参数名
	 */
	private String offsetParamName = "offset";

	/**
	 * 忽略大小写参数名后缀
	 */
	private String ignoreCaseParamNameSuffix = "_ic";

	/**
	 * 忽略大小写参数名后缀
	 */
	private String filterOperationParamNameSuffix = "_op";
	
	
	private String booleanTrueParamNameSuffix = "_true";
	
	
	private String booleanFalseParamNameSuffix = "_false";
	
	private String value2ParamNameSuffix = "_2";
	
	private String paramNameSeparator = "_";
	
	
	public String getMaxParamName() {
		return maxParamName;
	}
	
	/***
	 * url?name=jack&name_ic=false&sort=name&order=asc&max=10&offset=10
	 * url?name=&name_ic=true&name_op=ny
	 * url?name=jack&name_ic=true&name_op=sw&age=16&age_op=gt
	 * url?date=2017-06-01&date_bt=2017-06-05&data_op=bt
	 */
	@Override
	public SearchParam resolve(List<String> fieldList, Map<String, String> paraMap) {
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
			if (key.equals(maxParamName)) {
				searchParam.setMax(Integer.valueOf(value));
				continue;
			}
			if (key.equals(offsetParamName)) {
				searchParam.setOffset(Long.valueOf(value));
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

	public void setMaxParamName(String maxParamName) {
		this.maxParamName = maxParamName;
	}

	public void setOffsetParamName(String offsetParamName) {
		this.offsetParamName = offsetParamName;
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
	
}
