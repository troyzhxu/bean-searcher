package com.ejl.searcher.implement;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ejl.searcher.SearchParamResolver;
import com.ejl.searcher.implement.pagination.MaxOffsetPaginationResolver;
import com.ejl.searcher.implement.pagination.PaginationResolver;
import com.ejl.searcher.implement.parafilter.ParamFilter;
import com.ejl.searcher.param.FilterParam;
import com.ejl.searcher.param.Operator;
import com.ejl.searcher.param.SearchParam;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 *         默认查询参数解析器
 * 
 */
public class MainSearchParamResolver implements SearchParamResolver {

	Log log = LogFactory.getLog(MainSearchParamResolver.class);
	
	/**
	 * 默认最大条数
	 */
	private Integer defaultMax = 15;
	
	/**
	 * 排序字段参数名
	 */
	private String sortParamName = "sort";
	
	/**
	 * 排序方法字段参数名
	 */
	private String orderParamName = "order";
	
	/**
	 * 参数名分割符
	 * V1.2.0之前默认值是下划线："_"，自V1.2.0之后默认值更新为中划线："-"
	 */
	private String paramNameSeparator = "-";
	
	/**
	 * 忽略大小写参数名后缀
	 */
	private String ignoreCaseParamNameSuffix = "ic";

	/**
	 * 忽略大小写参数名后缀
	 */
	private String filterOperationParamNameSuffix = "op";
	
	/**
	 * Boolean true 值参数后缀
	 */
	private String booleanTrueParamNameSuffix = "true";
	
	/**
	 * Boolean false 值参数后缀
	 */
	private String booleanFalseParamNameSuffix = "false";

	
	
	private PaginationResolver paginationResolver = new MaxOffsetPaginationResolver();
	
	private final Pattern indexSuffixPattern = Pattern.compile("[0-9]+");

	private ParamFilter[] paramFilters;
	
	@Override
	public PaginationResolver getPaginationResolver() {
		return paginationResolver;
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
			String[] fieldSuffix = findFieldAndSuffix(fieldList, key);
			if (fieldSuffix == null || fieldSuffix.length == 0) {
				continue;
			}
			String field = fieldSuffix[0];
			String suffix = fieldSuffix.length > 1 ? fieldSuffix[1] : null;
			if (suffix == null) {
				FilterParam filterParam = findFilterParam(searchParam, key);
				filterParam.addValue(value);
				if (filterParam.getOperator() == null) {
					filterParam.setOperator(Operator.Equal);
				}
			} else if (booleanTrueParamNameSuffix.equals(suffix)) {
				FilterParam filterParam = findFilterParam(searchParam, field);
				filterParam.setOperator(Operator.Equal);
				filterParam.addValue("1");
			} else if (booleanFalseParamNameSuffix.equals(suffix)) {
				FilterParam filterParam = findFilterParam(searchParam, field);
				filterParam.setOperator(Operator.Equal);
				filterParam.addValue("0");
			} else if (ignoreCaseParamNameSuffix.equals(suffix)) {
				FilterParam filterParam = findFilterParam(searchParam, field);
				String upVal = value != null ? value.toUpperCase() : value;
				if ("0".equals(upVal) || "OFF".equals(upVal) || "FALSE".endsWith(upVal) || "N".endsWith(upVal)
						|| "NO".endsWith(upVal) || "F".endsWith(upVal)) {
					filterParam.setIgnoreCase(false);
				} else {
					filterParam.setIgnoreCase(true);
				}
			} else if (filterOperationParamNameSuffix.equals(suffix)) {
				FilterParam filterParam = findFilterParam(searchParam, field);
				filterParam.setOperator(Operator.from(value));
			} else {	// 多值解析
				try {
					int index = Integer.parseInt(suffix);
					FilterParam filterParam = findFilterParam(searchParam, field);
					filterParam.addValue(value, index);
				} catch (NumberFormatException e) {
					log.error("不能解析的查询参数名：" + key, e);
				}
			}
		}
		searchParam.removeUselessFilterParam();
		return searchParam;
	}

	private FilterParam findFilterParam(SearchParam searchParam, String field) {
		FilterParam filterParam = null;
		List<FilterParam> list = searchParam.getFilterParamList();
		for (FilterParam param : list) {
			if (param.getName().equals(field)) {
				filterParam = param;
				break;
			}
		}
		if (filterParam == null) {
			filterParam = new FilterParam();
			filterParam.setName(field);
			searchParam.addFilterParam(filterParam);
		}
		return filterParam;
	}
	
	private String[] findFieldAndSuffix(List<String> fieldList, String key) {
		for (String field : fieldList) {
			if (key.equals(field)) {
				return new String[]{field};
			}	
			if (key.startsWith(field + paramNameSeparator)) {
				String suffix = key.substring(field.length() + paramNameSeparator.length());
				if (filterOperationParamNameSuffix.equals(suffix) || ignoreCaseParamNameSuffix.equals(suffix) 
						|| booleanTrueParamNameSuffix.equals(suffix) || booleanFalseParamNameSuffix.equals(suffix)
						|| indexSuffixPattern.matcher(suffix).matches()) {
					return new String[]{field, suffix};
				}
			}
		}
		return null;
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

	public void setPaginationResolver(PaginationResolver paginationResolver) {
		this.paginationResolver = paginationResolver;
	}

	public void setParamFilters(ParamFilter[] paramFilters) {
		this.paramFilters = paramFilters;
	}
	
}
