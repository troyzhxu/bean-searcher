package com.ejlchina.searcher.implement;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.ejlchina.searcher.Metadata;
import com.ejlchina.searcher.util.MapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejlchina.searcher.ParamResolver;
import com.ejlchina.searcher.implement.pagination.MaxOffsetPagination;
import com.ejlchina.searcher.implement.pagination.Pagination;
import com.ejlchina.searcher.ParamFilter;
import com.ejlchina.searcher.FilterParam;
import com.ejlchina.searcher.param.Operator;
import com.ejlchina.searcher.SearchParam;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 默认查询参数解析器
 */
public class DefaultParamResolver implements ParamResolver {


	protected Logger log = LoggerFactory.getLogger(DefaultParamResolver.class);

	/**
	 * 默认最大条数
	 */
	private Integer defaultSize = 15;
	
	/**
	 * 排序字段参数名
	 */
	private String sortName = "sort";
	
	/**
	 * 排序方法字段参数名
	 */
	private String orderName = "order";
	
	/**
	 * 参数名分割符
	 * v1.2.0之前默认值是下划线："_"，自 v1.2.0之后默认值更新为中划线："-"
	 */
	private String separator = "-";
	
	/**
	 * 忽略大小写参数名后缀
	 * 带上该参数会导致字段索引不被使用，查询速度降低，在大数据量下建议使用数据库本身的字符集实现忽略大小写功能。
	 */
	private String ignoreCaseSuffix = "ic";

	/**
	 * 过滤运算符参数名后缀
	 */
	private String operatorSuffix = "op";
	
	/**
	 * Boolean true 值参数后缀
	 */
	private String trueSuffix = "true";
	
	/**
	 * Boolean false 值参数后缀
	 */
	private String falseSuffix = "false";


	private Pagination pagination = new MaxOffsetPagination();
	
	private final Pattern indexSuffixPattern = Pattern.compile("[0-9]+");

	private ParamFilter[] filters = new ParamFilter[] { };
	
	@Override
	public Pagination getPagination() {
		return pagination;
	}
	
	@Override
	public <T> SearchParam resolve(Metadata<T> metadata, Map<String, Object> paraMap) {
		for (ParamFilter paramFilter: filters) {
			if (paraMap == null) {
				break;
			}
			paraMap = paramFilter.doFilter(metadata, paraMap);
		}
		if (paraMap == null) {
			return doResolve(metadata, new HashMap<>());
		}
		return doResolve(metadata, paraMap);
	}

	private <T> SearchParam doResolve(Metadata<T> metadata, Map<String, Object> paraMap) {
		List<String> fieldList = metadata.getFieldList();
		SearchParam searchParam = new SearchParam(paraMap, defaultSize);
		for (Entry<String, Object> entry : paraMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (key.equals(sortName) && value instanceof String) {
				searchParam.setSort((String) value);
				continue;
			}
			if (key.equals(orderName) && value instanceof String) {
				searchParam.setOrder((String) value);
				continue;
			}
			if (pagination.paginate(searchParam, key, value)) {
				continue;
			}
			RawParam rawParam = resolveRawParam(fieldList, key);
			if (rawParam.type == RawParam.VERTUAL) {
				searchParam.putVirtualParam(key, value);
			} else if (rawParam.type == RawParam.FEILD) {
				FilterParam filterParam = findFilterParam(searchParam, key);
				filterParam.addValue(value);
				if (filterParam.getOperator() == null) {
					filterParam.setOperator(Operator.Equal);
				}
			} else if (trueSuffix.equals(rawParam.suffix)) {
				FilterParam filterParam = findFilterParam(searchParam, rawParam.field);
				filterParam.setOperator(Operator.Equal);
				filterParam.addValue("1");
			} else if (falseSuffix.equals(rawParam.suffix)) {
				FilterParam filterParam = findFilterParam(searchParam, rawParam.field);
				filterParam.setOperator(Operator.Equal);
				filterParam.addValue("0");
			} else if (ignoreCaseSuffix.equals(rawParam.suffix)) {
				FilterParam filterParam = findFilterParam(searchParam, rawParam.field);
				if (value != null) {
					if (value instanceof Boolean) {
						filterParam.setIgnoreCase((Boolean) value);
					} else
					if (value instanceof String) {
						String upcase = ((String) value).toUpperCase();
						filterParam.setIgnoreCase(!("0".equals(upcase) || "OFF".equals(upcase) || "FALSE".endsWith(upcase) || "N".endsWith(upcase)
								|| "NO".endsWith(upcase) || "F".endsWith(upcase)));
					}
				}
			} else if (operatorSuffix.equals(rawParam.suffix)) {
				FilterParam filterParam = findFilterParam(searchParam, rawParam.field);
				if (value instanceof Operator) {
					filterParam.setOperator((Operator) value);
				} else
				if (value instanceof String) {
					filterParam.setOperator(Operator.from((String) value));
				}
			} else {	// 多值解析
				try {
					int index = Integer.parseInt(rawParam.suffix);
					FilterParam filterParam = findFilterParam(searchParam, rawParam.field);
					if (filterParam.getOperator() == null) {
						filterParam.setOperator(Operator.Equal);
					}
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
	
	private RawParam resolveRawParam(List<String> fieldList, String key) {
		for (String field : fieldList) {
			if (key.equals(field)) {
				return new RawParam(RawParam.FEILD, field, null);
			}	
			if (key.startsWith(field + separator)) {
				String suffix = key.substring(field.length() + separator.length());
				if (operatorSuffix.equals(suffix) || ignoreCaseSuffix.equals(suffix)
						|| trueSuffix.equals(suffix) || falseSuffix.equals(suffix)
						|| indexSuffixPattern.matcher(suffix).matches()) {
					return new RawParam(RawParam.OPERATOR, field, suffix);
				}
			}
		}
		return new RawParam(RawParam.VERTUAL, key, null);
	}

	public void setDefaultSize(Integer defaultSize) {
		this.defaultSize = defaultSize;
	}
	
	public void setSortName(String sortName) {
		MapBuilder.config(MapBuilder.SORT, sortName);
		this.sortName = sortName;
	}

	public void setOrderName(String orderName) {
		MapBuilder.config(MapBuilder.ORDER, orderName);
		this.orderName = orderName;
	}

	public void setIgnoreCaseSuffix(String ignoreCaseSuffix) {
		MapBuilder.config(MapBuilder.IC_SUFFIX, ignoreCaseSuffix);
		this.ignoreCaseSuffix = ignoreCaseSuffix;
	}

	public void setOperatorSuffix(String operatorSuffix) {
		MapBuilder.config(MapBuilder.OP_SUFFIX, operatorSuffix);
		this.operatorSuffix = operatorSuffix;
	}

	public void setTrueSuffix(String trueSuffix) {
		this.trueSuffix = trueSuffix;
	}

	public void setFalseSuffix(String falseSuffix) {
		this.falseSuffix = falseSuffix;
	}

	public void setSeparator(String separator) {
		MapBuilder.config(MapBuilder.SEPARATOR, separator);
		this.separator = Objects.requireNonNull(separator);
	}

	public String getSeparator() {
		return separator;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = Objects.requireNonNull(pagination);
	}

	public void setFilters(ParamFilter[] filters) {
		this.filters = Objects.requireNonNull(filters);
	}

	public ParamFilter[] getFilters() {
		return filters;
	}

	static class RawParam {
		
		static final int FEILD = 1;
		static final int OPERATOR = 2;
		static final int VERTUAL = 3;
		
		int type;
		String suffix;
		String field;
		
		
		public RawParam(int type, String field, String suffix) {
			this.type = type;
			this.field = field;
			this.suffix = suffix;
		}
		
	}
	
	
}
