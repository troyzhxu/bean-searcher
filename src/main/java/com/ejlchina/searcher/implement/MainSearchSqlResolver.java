package com.ejlchina.searcher.implement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ejlchina.searcher.SearchSql;
import com.ejlchina.searcher.SearchSqlResolver;
import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.beanmap.SearchBeanMap;
import com.ejlchina.searcher.dialect.Dialect;
import com.ejlchina.searcher.dialect.Dialect.PaginateSql;
import com.ejlchina.searcher.param.FilterParam;
import com.ejlchina.searcher.param.Operator;
import com.ejlchina.searcher.param.SearchParam;
import com.ejlchina.searcher.util.StringUtils;
import com.ejlchina.searcher.virtual.VirtualParam;

/**
 * 默认查询SQL解析器
 * 
 * @author Troy.Zhou @ 2017-03-20
 * @since V1.1.1
 */
public class MainSearchSqlResolver implements SearchSqlResolver {

	
	static final Pattern DATE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

	static final Pattern DATE_HOUR_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}");
	
	static final Pattern DATE_MINUTE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}");
	
	static final Pattern DATE_SECOND_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}");


	/**
	 * 数据库方言
	 */
	private Dialect dialect;

	
	
	public MainSearchSqlResolver() {
	}
	
	public MainSearchSqlResolver(Dialect dialect) {
		this.dialect = dialect;
	}


	@Override
	public SearchSql resolve(SearchBeanMap searchBeanMap, SearchParam searchParam) {
		
		List<String> fieldList = searchBeanMap.getFieldList();
		Map<String, String> fieldDbMap = searchBeanMap.getFieldDbMap();
		Map<String, String> fieldDbAliasMap = searchBeanMap.getFieldDbAliasMap();
		Map<String, Class<?>> fieldTypeMap = searchBeanMap.getFieldTypeMap();
		
		Map<String, Object> virtualParamMap = searchParam.getVirtualParamMap();
		
		SearchSql searchSql = new SearchSql();
		StringBuilder builder = new StringBuilder("select ");
		if (searchBeanMap.isDistinct()) {
			builder.append("distinct ");
		}
		int fieldCount = fieldList.size();
		for (int i = 0; i < fieldCount; i++) {
			String field = fieldList.get(i);
			String dbField = fieldDbMap.get(field);
			String dbAlias = fieldDbAliasMap.get(field);
			
			List<VirtualParam> fieldVirtualParams = searchBeanMap.getFieldVirtualParams(field);
			if (fieldVirtualParams != null) {
				for (VirtualParam virtualParam: fieldVirtualParams) {
					Object sqlParam = virtualParamMap.get(virtualParam.getName());
					if (virtualParam.isParameterized()) {
						searchSql.addListSqlParam(sqlParam);
						// 只有在 distinct 条件，聚族查询 SQL 里才会出现 字段查询 语句，才需要将 虚拟参数放到 聚族参数里 
						if (searchBeanMap.isDistinct()) {
							searchSql.addClusterSqlParam(sqlParam);
						}
					} else {
						String strParam = sqlParam != null ? sqlParam.toString() : "null";
						dbField = dbField.replace(virtualParam.getSqlName(), strParam);
					}
				}
			}
			
			builder.append(dbField).append(" ").append(dbAlias);
			if (i < fieldCount - 1) {
				builder.append(", ");
			}
			
			searchSql.addListAlias(dbAlias);
		}
		String fieldSelectSql = builder.toString();

		builder = new StringBuilder(" from ");
		String talbes = searchBeanMap.getTalbes();
		
		List<VirtualParam> tableVirtualParams = searchBeanMap.getTableVirtualParams();
		if (tableVirtualParams != null) {
			for (VirtualParam virtualParam: tableVirtualParams) {
				Object sqlParam = virtualParamMap.get(virtualParam.getName());
				if (virtualParam.isParameterized()) {
					searchSql.addListSqlParam(sqlParam);
					searchSql.addClusterSqlParam(sqlParam);
				} else {
					String strParam = sqlParam != null ? sqlParam.toString() : "null";
					talbes = talbes.replace(virtualParam.getSqlName(), strParam);
				}
			}
		}
		builder.append(talbes);
		
		String joinCond = searchBeanMap.getJoinCond();
		boolean hasJoinCond = joinCond != null && !"".equals(joinCond.trim());
		List<FilterParam> filterParamList = searchParam.getFilterParamList();

		if (hasJoinCond || filterParamList.size() > 0) {
			builder.append(" where ");
			if (hasJoinCond) {
				builder.append("(");
				List<VirtualParam> joinCondVirtualParams = searchBeanMap.getJoinCondVirtualParams();
				if (joinCondVirtualParams != null) {
					for (VirtualParam virtualParam: joinCondVirtualParams) {
						Object sqlParam = virtualParamMap.get(virtualParam.getName());
						if (virtualParam.isParameterized()) {
							searchSql.addListSqlParam(sqlParam);
							searchSql.addClusterSqlParam(sqlParam);
						} else {
							String strParam = sqlParam != null ? sqlParam.toString() : "null";
							joinCond = joinCond.replace(virtualParam.getSqlName(), strParam);
						}
					}
				}
				builder.append(joinCond).append(")");
			}
		}
		for (int i = 0; i < filterParamList.size(); i++) {
			if (i > 0 || hasJoinCond) {
				builder.append(" and ");
			}
			FilterParam filterParam = filterParamList.get(i);
			String fieldName = filterParam.getName();
			List<Object> sqlParams = appendFilterConditionSql(builder, fieldTypeMap.get(fieldName), 
					fieldDbMap.get(fieldName), filterParam);
			for (Object sqlParam : sqlParams) {
				searchSql.addListSqlParam(sqlParam);
				searchSql.addClusterSqlParam(sqlParam);
			}
		}
		String groupBy = searchBeanMap.getGroupBy();
		String[] summaryFields = searchParam.getSummaryFields();
		boolean shouldQueryTotal = searchParam.isShouldQueryTotal();
		if (StringUtils.isBlank(groupBy)) {
			if (searchBeanMap.isDistinct()) {
				String originalSql = fieldSelectSql + builder.toString();
				String clusterSelectSql = resolveClusterSelectSql(fieldDbMap, searchSql, 
						summaryFields, shouldQueryTotal, originalSql);
				String tableAlias = generateTableAlias(originalSql);
				searchSql.setClusterSqlString(clusterSelectSql + " from (" + originalSql + ") " + tableAlias);
			} else {
				String fromWhereSql = builder.toString();
				String clusterSelectSql = resolveClusterSelectSql(fieldDbMap, searchSql, 
						summaryFields, shouldQueryTotal, fromWhereSql);
				searchSql.setClusterSqlString(clusterSelectSql + fromWhereSql);
			}
		} else {
			builder.append(" group by " + groupBy);
			String fromWhereSql = builder.toString();
			if (searchBeanMap.isDistinct()) {
				String originalSql = fieldSelectSql + fromWhereSql;
				String clusterSelectSql = resolveClusterSelectSql(fieldDbMap, searchSql, 
						summaryFields, shouldQueryTotal, originalSql);
				String tableAlias = generateTableAlias(originalSql);
				searchSql.setClusterSqlString(clusterSelectSql + " from (" + originalSql + ") " + tableAlias);
			} else {
				String clusterSelectSql = resolveClusterSelectSql(fieldDbMap, searchSql, 
						summaryFields, shouldQueryTotal, fromWhereSql);
				String tableAlias = generateTableAlias(fromWhereSql);
				searchSql.setClusterSqlString(clusterSelectSql + " from (select count(*) " + fromWhereSql + ") " + tableAlias);
			}
		}
		String sortDbAlias = fieldDbAliasMap.get(searchParam.getSort());
		if (sortDbAlias != null) {
			builder.append(" order by ").append(sortDbAlias);
			String order = searchParam.getOrder();
			if (order != null) {
				builder.append(" ").append(order);
			}
		}
		String fromWhereSql = builder.toString();
		PaginateSql paginateSql = dialect.forPaginate(fieldSelectSql, fromWhereSql, searchParam.getMax(),
				searchParam.getOffset());
		searchSql.setListSqlString(paginateSql.getSql());
		searchSql.addListSqlParams(paginateSql.getParams());
		return searchSql;
	}


	private String resolveClusterSelectSql(Map<String, String> fieldDbMap, 
			SearchSql searchSql, String[] summaryFields, boolean shouldQueryTotal, String originalSql) {
		StringBuilder clusterSelectSqlBuilder = new StringBuilder("select ");
		if (shouldQueryTotal) {
			String countAlias = generateColumnAlias("count", originalSql);
			clusterSelectSqlBuilder.append("count(*) ").append(countAlias);
			searchSql.setCountAlias(countAlias);
		}
		if (summaryFields != null) {
			if (shouldQueryTotal && summaryFields.length > 0) {
				clusterSelectSqlBuilder.append(", ");
			}
			for (int i = 0; i < summaryFields.length; i++) {
				String summaryField = summaryFields[i];
				String summaryAlias = generateColumnAlias(summaryField, originalSql);
				String dbField = fieldDbMap.get(summaryField);
				if (dbField == null) {
					throw new SearcherException("求和属性【" + summaryField + "】没有和数据库字段做映射，请检查该属性是否被@DbField正确注解！");
				}
				clusterSelectSqlBuilder.append("sum(").append(dbField)
					.append(") ").append(summaryAlias);
				if (i < summaryFields.length - 1) {
					clusterSelectSqlBuilder.append(", ");
				}
				searchSql.addSummaryAlias(summaryAlias);
			}
		}
		return clusterSelectSqlBuilder.toString();
	}
	

	private String generateTableAlias(String originalSql) {
		return generateAlias("tbl_", originalSql);
	}

	private String generateColumnAlias(String seed, String originalSql) {
		return generateAlias("col_" + seed, originalSql);
	}
	
	private String generateAlias(String seed, String originalSql) {
		int index = 0;
		String tableAlias = seed;
		while (originalSql.contains(tableAlias)) {
			tableAlias = seed + index++;
		}
		return tableAlias;
	}
	
	
	/**
	 * @return 查询参数值
	 */
	private List<Object> appendFilterConditionSql(StringBuilder builder, Class<?> fieldType, 
			String dbField, FilterParam filterParam) {
		Object[] values = filterParam.getValues();
		boolean ignoreCase = filterParam.isIgnoreCase();
		Operator operator = filterParam.getOperator();
		Object firstRealValue = filterParam.firstNotNullValue();
		
		if (Date.class.isAssignableFrom(fieldType)) {
			for (int i = 0; i < values.length; i++) {
				values[i] = dateValue(values[i]);
			}
			firstRealValue = dateValue(firstRealValue);
		}
		
		if (ignoreCase) {
			for (int i = 0; i < values.length; i++) {
				Object val = values[i];
				if (val != null) {
					if (val instanceof String) {
						values[i] = ((String) val).toUpperCase();
					} else {
						values[i] = val;
					}
				}
			}
			if (firstRealValue != null && firstRealValue instanceof String) {
				firstRealValue = ((String) firstRealValue).toUpperCase();
			}
		}
		if (operator != Operator.MultiValue) {
			if (ignoreCase) {
				dialect.toUpperCase(builder, dbField);
			} else {
				builder.append(dbField);
			}
		}
		List<Object> params = new ArrayList<>(2);
		switch (operator) {
		case Include:
			builder.append(" like ?");
			params.add("%" + firstRealValue + "%");
			break;
		case Equal:
			builder.append(" = ?");
			params.add(firstRealValue);
			break;
		case GreaterEqual:
			builder.append(" >= ?");
			params.add(firstRealValue);
			break;
		case GreaterThan:
			builder.append(" > ?");
			params.add(firstRealValue);
			break;
		case LessEqual:
			builder.append(" <= ?");
			params.add(firstRealValue);
			break;
		case LessThan:
			builder.append(" < ?");
			params.add(firstRealValue);
			break;
		case NotEqual:
			builder.append(" != ?");
			params.add(firstRealValue);
			break;
		case Empty:
			builder.append(" is null");
			break;
		case NotEmpty:
			builder.append(" is not null");
			break;
		case StartWith:
			builder.append(" like ?");
			params.add(firstRealValue + "%");
			break;
		case EndWith:
			builder.append(" like ?");
			params.add("%" + firstRealValue);
			break;
		case Between:
			boolean val1Null = false;
			boolean val2Null = false;
			Object value0 = values.length > 0 ? values[0] : null;
			Object value1 = values.length > 1 ? values[1] : null;
			if (value0 == null || (value0 instanceof String && StringUtils.isBlank((String) value0))) {
				val1Null = true;
			}
			if (value1 == null || (value1 instanceof String && StringUtils.isBlank((String) value1))) {
				val2Null = true;
			}
			if (!val1Null && !val2Null) {
				builder.append(" between ? and ? ");
				params.add(value0);
				params.add(value1);
			} else if (val1Null && !val2Null) {
				builder.append(" <= ? ");
				params.add(value1);
			} else if (!val1Null && val2Null) {
				builder.append(" >= ? ");
				params.add(value0);
			}
			break;
		case MultiValue:
			builder.append("(");
			for (int i = 0; i < values.length; i++) {
				Object value = values[i];
				if (value == null) {
					builder.append(dbField).append(" is null");
				} else if (ignoreCase) {
					dialect.toUpperCase(builder, dbField);
					builder.append(" = ?");
					params.add(value);
				} else if (Date.class.isAssignableFrom(fieldType)) {
					builder.append(dbField).append(" = ?");
					params.add(value);
				} else {
					builder.append(dbField).append(" = ?");
					params.add(value);
				}
				if (i < values.length - 1) {
					builder.append(" or ");
				}
			}
			builder.append(")");
			break;
		}
		return params;
	}

	private Object dateValue(Object value) {
		if (value != null && value instanceof String) {
			String strValue = (String) value;
			if (DATE_PATTERN.matcher(strValue).matches()) {
				return strValue + " 00:00:00";
			} else if (DATE_HOUR_PATTERN.matcher(strValue).matches()) {
				return strValue + ":00:00";
			} else if (DATE_MINUTE_PATTERN.matcher(strValue).matches()) {
				return strValue + ":00";
			}
		}
		return value;
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

}
