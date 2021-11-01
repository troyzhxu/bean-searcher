package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.param.FetchType;
import com.ejlchina.searcher.param.FieldParam;
import com.ejlchina.searcher.param.Operator;
import com.ejlchina.searcher.param.OrderParam;
import com.ejlchina.searcher.util.MapBuilder;
import com.ejlchina.searcher.util.ObjectUtils;

import java.util.*;
import java.util.regex.Pattern;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 默认查询参数解析器
 */
public class DefaultParamResolver implements ParamResolver {

	public final Pattern INDEX_PATTERN = Pattern.compile("[0-9]+");

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

	private PageExtractor pageExtractor = new PageOffsetExtractor();

	private ParamFilter[] filters = new ParamFilter[] { new BoolValueFilter() };

	@Override
	public <T> SearchParam resolve(Metadata<T> metadata, FetchType fetchType, Map<String, Object> paraMap) {
		for (ParamFilter paramFilter: filters) {
			if (paraMap == null) {
				break;
			}
			paraMap = paramFilter.doFilter(metadata, paraMap);
		}
		if (paraMap == null) {
			paraMap = new HashMap<>();
		}
		return doResolve(metadata.getFieldList(), fetchType, paraMap);
	}

	protected <T> SearchParam doResolve(List<String> fieldList, FetchType fetchType, Map<String, Object> paraMap) {
		List<FieldParam> fieldParams = resolveFieldParams(fieldList, paraMap);
		SearchParam searchParam = new SearchParam(paraMap, fetchType, fieldParams);
		if (!fetchType.isFetchAll()) {
			searchParam.setPageParam(pageExtractor.extract(paraMap));
		}
		searchParam.setOrderParam(resolveOrderParam(paraMap));
		return searchParam;
	}

	protected List<FieldParam> resolveFieldParams(List<String> fieldList, Map<String, Object> paraMap) {
		Map<String, List<Integer>> fieldIndicesMap = new HashMap<>();
		for (String key : paraMap.keySet()) {
			int index = key.lastIndexOf(separator);
			if (index > 0 && key.length() > index + 1) {
				String suffix = key.substring(index + 1);
				if (INDEX_PATTERN.matcher(suffix).matches()) {
					String field = key.substring(0, index);
					mapFieldIndex(fieldIndicesMap, field, Integer.parseInt(suffix));
				}
			}
			mapFieldIndex(fieldIndicesMap, key, 0);
		}
		List<FieldParam> fieldParams = new ArrayList<>();
		for (String field : fieldList) {
			List<Integer> indices = fieldIndicesMap.get(field);
			FieldParam param = toFieldParam(field, indices, paraMap);
			if (param != null) {
				fieldParams.add(param);
			}
		}
		return fieldParams;
	}

	protected void mapFieldIndex(Map<String, List<Integer>> fieldIndicesKeysMap, String field, int index) {
		fieldIndicesKeysMap.computeIfAbsent(field, k -> new ArrayList<>(2)).add(index);
	}

	protected FieldParam toFieldParam(String field, List<Integer> indices, Map<String, Object> paraMap) {
		Operator operator = toOperator(paraMap.get(field + separator + operatorSuffix));
		if (operator == Operator.Empty || operator == Operator.NotEmpty) {
			return new FieldParam(field, operator);
		}
		if (indices != null && indices.size() > 0) {
			FieldParam param = new FieldParam(field, operator);
			param.setIgnoreCase(ObjectUtils.toBoolean(paraMap.get(field + separator + ignoreCaseSuffix)));
			for (int index : indices) {
				Object value = paraMap.get(field + separator + index);
				if (value != null) {
					param.addValue(value, index);
				}
			}
			Object value = paraMap.get(field);
			if (value != null) {
				param.addValue(value, 0);
			}
			return param;
		}
		return null;
	}

	protected Operator toOperator(Object value) {
		if (value instanceof Operator) {
			return (Operator) value;
		}
		if (value instanceof String) {
			return Operator.from((String) value);
		}
		return Operator.Equal;
	}

	private OrderParam resolveOrderParam(Map<String, Object> paraMap) {
		String sort = ObjectUtils.string(paraMap.get(sortName));
		String order = ObjectUtils.string(paraMap.get(orderName));
		if (sort != null) {
			return new OrderParam(sort, order);
		}
		return null;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		MapBuilder.config(MapBuilder.SORT, sortName);
		this.sortName = Objects.requireNonNull(sortName);
	}

	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		MapBuilder.config(MapBuilder.ORDER, orderName);
		this.orderName = Objects.requireNonNull(orderName);
	}

	public String getIgnoreCaseSuffix() {
		return ignoreCaseSuffix;
	}

	public void setIgnoreCaseSuffix(String ignoreCaseSuffix) {
		MapBuilder.config(MapBuilder.IC_SUFFIX, ignoreCaseSuffix);
		this.ignoreCaseSuffix = Objects.requireNonNull(ignoreCaseSuffix);
	}

	public String getOperatorSuffix() {
		return operatorSuffix;
	}

	public void setOperatorSuffix(String operatorSuffix) {
		MapBuilder.config(MapBuilder.OP_SUFFIX, operatorSuffix);
		this.operatorSuffix = Objects.requireNonNull(operatorSuffix);
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		MapBuilder.config(MapBuilder.SEPARATOR, separator);
		this.separator = Objects.requireNonNull(separator);
	}

	public PageExtractor getPageExtractor() {
		return pageExtractor;
	}

	public void setPageExtractor(PageExtractor pageExtractor) {
		this.pageExtractor = Objects.requireNonNull(pageExtractor);
	}

	public void setFilters(ParamFilter[] filters) {
		this.filters = Objects.requireNonNull(filters);
	}

	public ParamFilter[] getFilters() {
		return filters;
	}

}
