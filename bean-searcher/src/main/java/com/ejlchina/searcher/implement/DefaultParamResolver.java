package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.param.*;
import com.ejlchina.searcher.util.MapBuilder;
import com.ejlchina.searcher.util.ObjectUtils;
import com.ejlchina.searcher.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 默认查询参数解析器
 */
public class DefaultParamResolver implements ParamResolver {

	public static final Pattern INDEX_PATTERN = Pattern.compile("[0-9]+");

	/**
	 * 分页参数提取器
	 */
	private PageExtractor pageExtractor = new PageOffsetExtractor();

	/**
	 * 参数过滤器
	 */
	private ParamFilter[] paramFilters = new ParamFilter[] { new BoolValueFilter() };

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
	 * 用于指定只 Select 某些字段的参数名
	 */
	private String onlySelectName = "onlySelect";

	/**
	 * 用于指定不需要 Select 的字段的参数名
	 */
	private String selectExcludeName = "selectExclude";


	@Override
	public SearchParam resolve(BeanMeta<?> beanMeta, FetchType fetchType, Map<String, Object> paraMap) {
		for (ParamFilter filter: paramFilters) {
			if (paraMap == null) {
				break;
			}
			paraMap = filter.doFilter(beanMeta, paraMap);
		}
		if (paraMap == null) {
			paraMap = new HashMap<>();
		}
		return doResolve(beanMeta, fetchType, paraMap);
	}

	protected SearchParam doResolve(BeanMeta<?> beanMeta, FetchType fetchType, Map<String, Object> paraMap) {
		SearchParam searchParam = new SearchParam(paraMap, fetchType,
				resolveFetchFields(beanMeta, fetchType, paraMap),
				resolveFieldParams(beanMeta.getFieldMetas(), paraMap)
		);
		if (fetchType.canPaging()) {
			Paging paging = pageExtractor.extract(paraMap);
			if (fetchType.isFetchFirst()) {
				paging.setSize(1);
			}
			searchParam.setPaging(paging);
		}
		if (fetchType.shouldQueryList()) {
			// 只有列表检索，才需要排序
			searchParam.setOrderBy(resolveOrderBy(beanMeta.getFieldSet(), paraMap));
		}
		return searchParam;
	}

	protected List<String> resolveFetchFields(BeanMeta<?> beanMeta, FetchType fetchType, Map<String, Object> paraMap) {
		if (fetchType.shouldQueryList() || beanMeta.isDistinct() || StringUtils.isNotBlank(beanMeta.getGroupBy())) {
			Set<String> fieldList = beanMeta.getFieldSet();
			List<String> onlySelect = toList(paraMap.get(onlySelectName)).stream()
					.filter(fieldList::contains)
					.collect(Collectors.toList());
			List<String> selectExclude = toList(paraMap.get(selectExcludeName));
			return (onlySelect.isEmpty() ? fieldList : onlySelect).stream()
					.filter(f -> !selectExclude.contains(f))
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}


	protected List<String> toList(Object value) {
		return ObjectUtils.toList(value);
	}


	protected List<FieldParam> resolveFieldParams(Collection<FieldMeta> fieldMetas, Map<String, Object> paraMap) {
		Map<String, Set<Integer>> fieldIndicesMap = new HashMap<>();
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
		for (FieldMeta meta : fieldMetas) {
			if (!meta.isConditional()) {
				continue;
			}
			Set<Integer> indices = fieldIndicesMap.get(meta.getName());
			FieldParam param = toFieldParam(meta, indices, paraMap);
			if (param != null) {
				fieldParams.add(param);
			}
		}
		return fieldParams;
	}

	protected void mapFieldIndex(Map<String, Set<Integer>> fieldIndicesKeysMap, String field, int index) {
		fieldIndicesKeysMap.computeIfAbsent(field, k -> new HashSet<>(2)).add(index);
	}

	protected FieldParam toFieldParam(FieldMeta meta, Set<Integer> indices, Map<String, Object> paraMap) {
		String field = meta.getName();
		Object opValue = paraMap.get(field + separator + operatorSuffix);
		Operator operator = allowedOperator(opValue, meta.getOnlyOn());
		if (operator == null) {
			// 表示该字段不支持 opValue 的检索
			return null;
		}
		if (operator == Operator.Empty || operator == Operator.NotEmpty) {
			return new FieldParam(field, operator);
		}
		if (indices == null || indices.isEmpty()) {
			return null;
		}
		List<FieldParam.Value> values = new ArrayList<>();
		for (int index : indices) {
			Object value = paraMap.get(field + separator + index);
			if (index == 0 && value == null) {
				value = paraMap.get(field);
			}
			values.add(new FieldParam.Value(value, index));
		}
		if (isAllEmpty(values)) {
			return null;
		}
		boolean ignoreCase = ObjectUtils.toBoolean(paraMap.get(field + separator + ignoreCaseSuffix));
		return new FieldParam(field, operator, values, ignoreCase);
	}

	private boolean isAllEmpty(List<FieldParam.Value> values) {
		for (FieldParam.Value value : values) {
			if (!value.isEmptyValue()) {
				return false;
			}
		}
		return true;
	}

	protected Operator allowedOperator(Object value, Operator[] onlyOn) {
		Operator op = null;
		if (value instanceof Operator) {
			op = (Operator) value;
		}
		if (value instanceof String) {
			op = Operator.from((String) value);
		}
		if (op == null) {
			if (onlyOn.length == 0) {
				// 为空，代表没有约束，则缺省使用 Equal
				return Operator.Equal;
			}
			// 当指定 onlyOn 时，缺省使用 onlyOn 的第一个运算符
			return onlyOn[0];
		}
		if (onlyOn.length == 0) {
			return op;
		}
		for (Operator o : onlyOn) {
			if (o == op) {
				return op;
			}
		}
		// op 不在 onlyOn 的允许范围内，返回 null
		return null;
	}

	private OrderBy resolveOrderBy(Set<String> fieldSet, Map<String, Object> paraMap) {
		String sort = ObjectUtils.string(paraMap.get(sortName));
		String order = ObjectUtils.string(paraMap.get(orderName));
		if (sort != null && fieldSet.contains(sort)) {
			if ("asc".equalsIgnoreCase(order)) {
				return new OrderBy(sort, "asc");
			}
			if ("desc".equalsIgnoreCase(order)) {
				return new OrderBy(sort, "desc");
			}
			return new OrderBy(sort, null);
		}
		return null;
	}

	public PageExtractor getPageExtractor() {
		return pageExtractor;
	}

	public void setPageExtractor(PageExtractor pageExtractor) {
		this.pageExtractor = Objects.requireNonNull(pageExtractor);
	}

	public void setParamFilters(ParamFilter[] paramFilters) {
		this.paramFilters = Objects.requireNonNull(paramFilters);
	}

	public ParamFilter[] getParamFilters() {
		return paramFilters;
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

	public String getOnlySelectName() {
		return onlySelectName;
	}

	public void setOnlySelectName(String onlySelectName) {
		this.onlySelectName = onlySelectName;
	}

	public String getSelectExcludeName() {
		return selectExcludeName;
	}

	public void setSelectExcludeName(String selectExcludeName) {
		this.selectExcludeName = selectExcludeName;
	}

}
