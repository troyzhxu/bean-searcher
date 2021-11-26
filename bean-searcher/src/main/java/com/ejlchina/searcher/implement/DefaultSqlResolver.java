package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.constants.BeanSearcherConstant;
import com.ejlchina.searcher.constants.BeanSearcherConstant.Symbol;
import java.util.*;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.dialect.Dialect;
import com.ejlchina.searcher.dialect.Dialect.PaginateSql;
import com.ejlchina.searcher.dialect.MySqlDialect;
import com.ejlchina.searcher.param.*;
import com.ejlchina.searcher.SearchParam;
import com.ejlchina.searcher.util.ObjectUtils;
import com.ejlchina.searcher.util.StringUtils;
import java.util.regex.Pattern;

/**
 * 默认 SQL 解析器
 * 
 * @author Troy.Zhou @ 2017-03-20
 * @since v1.1.1
 */
public class DefaultSqlResolver implements SqlResolver {

	/**
	 * 数据库方言
	 */
	private Dialect dialect = new MySqlDialect();

	/**
	 * 日期参数矫正器
	 */
	private DateValueCorrector dateValueCorrector = new DateValueCorrector();
	
	
	public DefaultSqlResolver() {
	}

	public DefaultSqlResolver(Dialect dialect, DateValueCorrector dateValueCorrector) {
		this.dialect = dialect;
		this.dateValueCorrector = dateValueCorrector;
	}


	@Override
	public <T> SearchSql<T> resolve(BeanMeta<T> beanMeta, SearchParam searchParam) {
		List<String> fetchFields = searchParam.getFetchFields();
		SearchSql<T> searchSql = new SearchSql<>(beanMeta, fetchFields);

		FetchType fetchType = searchParam.getFetchType();
		searchSql.setShouldQueryCluster(fetchType.shouldQueryCluster());
		searchSql.setShouldQueryList(fetchType.shouldQueryList());

		StringBuilder builder = new StringBuilder("select ");
		if (beanMeta.isDistinct()) {
			builder.append("distinct ");
		}
		int fieldCount = fetchFields.size();
		for (int i = 0; i < fieldCount; i++) {
			String field = fetchFields.get(i);
			FieldMeta meta = beanMeta.requireFieldMeta(field);
			String dbField = resolveDbField(meta.getFieldSql(), searchParam, searchSql, beanMeta.isDistinct());
			builder.append(dbField).append(" ").append(meta.getDbAlias());
			if (i < fieldCount - 1) {
				builder.append(", ");
			}
		}
		String fieldSelectSql = builder.toString();

		builder = new StringBuilder(" from ")
				.append(resolveTables(beanMeta.getTableSnippet(), searchParam, searchSql));
		
		String joinCond = beanMeta.getJoinCond();
		boolean hasJoinCond = StringUtils.isNotBlank(joinCond);

		List<FieldParam> fieldParamList = searchParam.getFieldParams();

		if (hasJoinCond || fieldParamList.size() > 0) {
			builder.append(" where (");
			if (hasJoinCond) {
				List<SqlSnippet.Param> joinCondParams = beanMeta.getJoinCondEmbedParams();
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

		for (int i = 0; i < fieldParamList.size(); i++) {
			if (i > 0 || hasJoinCond) {
				builder.append(" and (");
			}
			FieldParam fieldParam = fieldParamList.get(i);
			String fieldName = fieldParam.getName();
			// 这里没取字段别名，因为在 count SQL 里，select 语句中可能没这个字段
			FieldMeta meta = beanMeta.requireFieldMeta(fieldName);
			List<Object> sqlParams = appendFilterConditionSql(builder, meta.getType(),
					meta.getFieldSql().getSnippet(), fieldParam);
			for (Object sqlParam : sqlParams) {
				searchSql.addListSqlParam(sqlParam);
				searchSql.addClusterSqlParam(sqlParam);
			}
			builder.append(")");
		}

		String groupBy = beanMeta.getGroupBy();
		String[] summaryFields = fetchType.getSummaryFields();
		boolean shouldQueryTotal = fetchType.shouldQueryTotal();
		if (StringUtils.isBlank(groupBy)) {
			if (shouldQueryTotal || summaryFields.length > 0) {
				if (beanMeta.isDistinct()) {
					String originalSql = fieldSelectSql + builder;
					String clusterSelectSql = resolveClusterSelectSql(searchSql, summaryFields, shouldQueryTotal, originalSql);
					String tableAlias = generateTableAlias(originalSql);
					searchSql.setClusterSqlString(clusterSelectSql + " from (" + originalSql + ") " + tableAlias);
				} else {
					String fromWhereSql = builder.toString();
					String clusterSelectSql = resolveClusterSelectSql(searchSql, summaryFields, shouldQueryTotal, fromWhereSql);
					searchSql.setClusterSqlString(clusterSelectSql + fromWhereSql);
				}
			}
		} else {
			List<SqlSnippet.Param> groupParams = beanMeta.getGroupByEmbedParams();
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
			if (shouldQueryTotal || summaryFields.length > 0) {
				String fromWhereSql = builder.toString();
				if (beanMeta.isDistinct()) {
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
		}
		if (fetchType.shouldQueryList()) {
			OrderBy orderBy = searchParam.getOrderBy();
			if (orderBy != null) {
				FieldMeta meta = beanMeta.requireFieldMeta(orderBy.getSort());
				builder.append(" order by ").append(meta.getDbAlias());
				String order = orderBy.getOrder();
				if (order != null) {
					builder.append(" ").append(order);
				}
			}
			String fromWhereSql = builder.toString();
			PaginateSql paginateSql = dialect.forPaginate(fieldSelectSql, fromWhereSql, searchParam.getPaging());
			searchSql.setListSqlString(paginateSql.getSql());
			searchSql.addListSqlParams(paginateSql.getParams());
		}
		return searchSql;
	}

	private <T> String resolveTables(SqlSnippet tableSnippet, SearchParam searchParam, SearchSql<T> searchSql) {
		String tables = tableSnippet.getSnippet();
		List<SqlSnippet.Param> params = tableSnippet.getParams();
		for (SqlSnippet.Param param : params) {
			Object sqlParam = searchParam.getPara(param.getName());
			if (param.isJdbcPara()) {
				searchSql.addListSqlParam(sqlParam);
				searchSql.addClusterSqlParam(sqlParam);
			} else {
				String strParam = sqlParam != null ? sqlParam.toString() : "";
				tables = tables.replace(param.getSqlName(), strParam);
			}
		}
		return tables;
	}

	private <T> String resolveDbField(SqlSnippet dbFieldSnippet, SearchParam searchParam, SearchSql<T> searchSql, boolean distinct) {
		String dbField = dbFieldSnippet.getSnippet();
		List<SqlSnippet.Param> params = dbFieldSnippet.getParams();
		for (SqlSnippet.Param param : params) {
			Object sqlParam = searchParam.getPara(param.getName());
			if (param.isJdbcPara()) {
				searchSql.addListSqlParam(sqlParam);
				// 只有在 distinct 条件，聚族查询 SQL 里才会出现 字段查询 语句，才需要将 内嵌参数放到 聚族参数里
				if (distinct) {
					searchSql.addClusterSqlParam(sqlParam);
				}
			} else {
				String strParam = sqlParam != null ? sqlParam.toString() : "";
				dbField = dbField.replace(param.getSqlName(), strParam);
			}
		}
		return dbField;
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
			BeanMeta<T> beanMeta = searchSql.getBeanMeta();
			if (shouldQueryTotal && summaryFields.length > 0) {
				clusterSelectSqlBuilder.append(", ");
			}
			for (int i = 0; i < summaryFields.length; i++) {
				String summaryField = summaryFields[i];
				String summaryAlias = generateColumnAlias(summaryField, originalSql);
				String fieldSql = beanMeta.getFieldSql(summaryField);
				if (fieldSql == null) {
					throw new SearchException("求和属性【" + summaryField + "】没有和数据库字段做映射，请检查该属性是否被 @DbField 正确注解！");
				}
				clusterSelectSqlBuilder.append("sum(").append(fieldSql)
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
		return generateAlias("t_", originalSql);
	}

	private String generateColumnAlias(String seed, String originalSql) {
		return generateAlias("_" + seed, originalSql);
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
			String dbField, FieldParam fieldParam) {
		Object[] values = fieldParam.getValues();
		boolean ignoreCase = fieldParam.isIgnoreCase();
		Operator operator = fieldParam.getOperator();
		if (Date.class.isAssignableFrom(fieldType)) {
			values = dateValueCorrector.correct(values, operator);
		}
		if (ignoreCase) {
			dialect.toUpperCase(builder, dbField);
			values = toUpperCase(values);
		} else {
			builder.append(dbField);
		}
		Object firstRealValue = ObjectUtils.firstNotNull(values);
		List<Object> params = new ArrayList<>(2);
		switch (operator) {
		case Like:
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
			builder.append(" is null or ").append(dbField).append(" = ''"); ;
			break;
		case NotEmpty:
			builder.append(" is not null and ").append(dbField).append(" != ''");
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
		case In:
		case MultiValue:
			builder.append(" in (");
			//多个参数的情况
			Object[] inValues = values;
			if(values.length == 1) {
				//1个参数分隔的情况
				Object[] infinalValues = values;
				String inVal = infinalValues[0].toString();
				if(inVal.contains(Symbol.SEMICOLON)) {
					inValues = inVal.split(Symbol.SEMICOLON, -1);
				}
			}
			for (int i = 0; i < inValues.length; i++) {
				builder.append("?");
				params.add(inValues[i]);
				if (i < inValues.length - 1) {
					builder.append(", ");
				}
			}
			builder.append(")");
			break;
		}
		return params;
	}

	public Object[] toUpperCase(Object[] params) {
		for (int i = 0; i < params.length; i++) {
			Object val = params[i];
			if (val != null) {
				if (val instanceof String) {
					params[i] = ((String) val).toUpperCase();
				} else {
					params[i] = val;
				}
			}
		}
		return params;
	}
	
	public Dialect getDialect() {
		return dialect;
	}
	
	public void setDialect(Dialect dialect) {
		this.dialect = Objects.requireNonNull(dialect);
	}

	public DateValueCorrector getDateValueCorrector() {
		return dateValueCorrector;
	}

	public void setDateValueCorrector(DateValueCorrector dateValueCorrector) {
		this.dateValueCorrector = Objects.requireNonNull(dateValueCorrector);
	}

}
