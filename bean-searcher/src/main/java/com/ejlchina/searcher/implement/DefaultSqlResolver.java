package com.ejlchina.searcher.implement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.dialect.Dialect;
import com.ejlchina.searcher.dialect.Dialect.PaginateSql;
import com.ejlchina.searcher.implement.processor.ParamProcessor;
import com.ejlchina.searcher.param.Operator;
import com.ejlchina.searcher.util.ObjectUtils;
import com.ejlchina.searcher.util.StringUtils;

/**
 * 默认查询SQL解析器
 * 
 * @author Troy.Zhou @ 2017-03-20
 * @since V1.1.1
 */
public class DefaultSqlResolver implements SqlResolver {

	/**
	 * 数据库方言
	 */
	private Dialect dialect;

	/**
	 * 参数处理器
	 */
	private ParamProcessor paramProcessor;
	
	
	public DefaultSqlResolver() {
	}

	public DefaultSqlResolver(Dialect dialect, ParamProcessor paramProcessor) {
		this.dialect = dialect;
		this.paramProcessor = paramProcessor;
	}


	@Override
	public <T> SearchSql<T> resolve(Metadata<T> metadata, SearchParam searchParam) {
		List<String> fieldList = metadata.getFieldList();
		Map<String, String> fieldDbAliasMap = metadata.getFieldDbAliasMap();
		Map<String, Class<?>> fieldTypeMap = metadata.getFieldTypeMap();
		
		SearchSql<T> searchSql = new SearchSql<>(metadata);
		StringBuilder builder = new StringBuilder("select ");
		if (metadata.isDistinct()) {
			builder.append("distinct ");
		}
		int fieldCount = fieldList.size();
		for (int i = 0; i < fieldCount; i++) {
			String field = fieldList.get(i);

			String dbField = metadata.getDbField(field);
			String dbAlias = fieldDbAliasMap.get(field);
			
			List<SqlSnippet.Param> fieldParams = metadata.getFieldEmbedParams(field);
			if (fieldParams != null) {
				for (SqlSnippet.Param param : fieldParams) {
					Object sqlParam = searchParam.getPara(param.getName());
					if (param.isJdbcPara()) {
						searchSql.addListSqlParam(sqlParam);
						// 只有在 distinct 条件，聚族查询 SQL 里才会出现 字段查询 语句，才需要将 虚拟参数放到 聚族参数里 
						if (metadata.isDistinct()) {
							searchSql.addClusterSqlParam(sqlParam);
						}
					} else {
						String strParam = sqlParam != null ? sqlParam.toString() : "";
						dbField = dbField.replace(param.getSqlName(), strParam);
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
		String talbes = metadata.getTalbes();
		
		List<SqlSnippet.Param> tableParams = metadata.getTableEmbedParams();
		if (tableParams != null) {
			for (SqlSnippet.Param param : tableParams) {
				Object sqlParam = searchParam.getPara(param.getName());
				if (param.isJdbcPara()) {
					searchSql.addListSqlParam(sqlParam);
					searchSql.addClusterSqlParam(sqlParam);
				} else {
					String strParam = sqlParam != null ? sqlParam.toString() : "";
					talbes = talbes.replace(param.getSqlName(), strParam);
				}
			}
		}
		builder.append(talbes);
		
		String joinCond = metadata.getJoinCond();
		boolean hasJoinCond = joinCond != null && !"".equals(joinCond.trim());
		List<FilterParam> filterParamList = searchParam.getFilterParamList();

		if (hasJoinCond || filterParamList.size() > 0) {
			builder.append(" where ");
			if (hasJoinCond) {
				builder.append("(");
				List<SqlSnippet.Param> joinCondParams = metadata.getJoinCondEmbedParams();
				if (joinCondParams != null) {
					for (SqlSnippet.Param param : joinCondParams) {
						Object sqlParam = searchParam.getPara(param.getName());
						if (param.isJdbcPara()) {
							searchSql.addListSqlParam(sqlParam);
							searchSql.addClusterSqlParam(sqlParam);
						} else {
							String strParam = sqlParam != null ? sqlParam.toString() : "";
							joinCond = joinCond.replace(param.getSqlName(), strParam);
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
					metadata.getDbField(fieldName), filterParam);
			for (Object sqlParam : sqlParams) {
				searchSql.addListSqlParam(sqlParam);
				searchSql.addClusterSqlParam(sqlParam);
			}
		}
		String groupBy = metadata.getGroupBy();
		String[] summaryFields = searchParam.getSummaryFields();
		boolean shouldQueryTotal = searchParam.isShouldQueryTotal();
		if (StringUtils.isBlank(groupBy)) {
			if (metadata.isDistinct()) {
				String originalSql = fieldSelectSql + builder;
				String clusterSelectSql = resolveClusterSelectSql(searchSql, summaryFields, shouldQueryTotal, originalSql);
				String tableAlias = generateTableAlias(originalSql);
				searchSql.setClusterSqlString(clusterSelectSql + " from (" + originalSql + ") " + tableAlias);
			} else {
				String fromWhereSql = builder.toString();
				String clusterSelectSql = resolveClusterSelectSql(searchSql, summaryFields, shouldQueryTotal, fromWhereSql);
				searchSql.setClusterSqlString(clusterSelectSql + fromWhereSql);
			}
		} else {
			List<SqlSnippet.Param> groupParams = metadata.getGroupByEmbedParams();
			if (groupParams != null) {
				for (SqlSnippet.Param param : groupParams) {
					Object sqlParam = searchParam.getPara(param.getName());
					if (param.isJdbcPara()) {
						searchSql.addListSqlParam(sqlParam);
						searchSql.addClusterSqlParam(sqlParam);
					} else {
						String strParam = sqlParam != null ? sqlParam.toString() : "";
						groupBy = groupBy.replace(param.getSqlName(), strParam);
					}
				}
			}
			builder.append(" group by ").append(groupBy);
			String fromWhereSql = builder.toString();
			if (metadata.isDistinct()) {
				String originalSql = fieldSelectSql + fromWhereSql;
				String clusterSelectSql = resolveClusterSelectSql(searchSql, summaryFields, shouldQueryTotal, originalSql);
				String tableAlias = generateTableAlias(originalSql);
				searchSql.setClusterSqlString(clusterSelectSql + " from (" + originalSql + ") " + tableAlias);
			} else {
				String clusterSelectSql = resolveClusterSelectSql(searchSql, summaryFields, shouldQueryTotal, fromWhereSql);
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


	private <T> String resolveClusterSelectSql(SearchSql<T> searchSql, String[] summaryFields,
				boolean shouldQueryTotal, String originalSql) {
		StringBuilder clusterSelectSqlBuilder = new StringBuilder("select ");
		if (shouldQueryTotal) {
			String countAlias = generateColumnAlias("count", originalSql);
			clusterSelectSqlBuilder.append("count(*) ").append(countAlias);
			searchSql.setCountAlias(countAlias);
		}
		if (summaryFields != null) {
			Metadata<T> metadata = searchSql.getMetadata();
			if (shouldQueryTotal && summaryFields.length > 0) {
				clusterSelectSqlBuilder.append(", ");
			}
			for (int i = 0; i < summaryFields.length; i++) {
				String summaryField = summaryFields[i];
				String summaryAlias = generateColumnAlias(summaryField, originalSql);
				String dbField = metadata.getDbField(summaryField);
				if (dbField == null) {
					throw new SearchException("求和属性【" + summaryField + "】没有和数据库字段做映射，请检查该属性是否被 @DbField 正确注解！");
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
		
		if (Date.class.isAssignableFrom(fieldType)) {
			values = paramProcessor.dateParams(values, operator);
		}
		if (ignoreCase) {
			values = paramProcessor.upperCase(values);
		}
		Object firstRealValue = ObjectUtils.firstNotNull(values);
		
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
			} else if (!val1Null) {
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

	
	public Dialect getDialect() {
		return dialect;
	}
	
	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	public ParamProcessor getParamProcessor() {
		return paramProcessor;
	}

	public void setParamProcessor(ParamProcessor paramProcessor) {
		this.paramProcessor = paramProcessor;
	}

}
