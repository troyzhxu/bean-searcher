package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.dialect.Dialect;
import com.ejlchina.searcher.dialect.Dialect.PaginateSql;
import com.ejlchina.searcher.param.FetchType;
import com.ejlchina.searcher.param.FieldParam;
import com.ejlchina.searcher.param.OrderBy;
import com.ejlchina.searcher.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 默认 SQL 解析器
 * 
 * @author Troy.Zhou @ 2017-03-20
 * @since v1.1.1
 */
public class DefaultSqlResolver extends DialectWrapper implements SqlResolver {

	/**
	 * 日期参数矫正器
	 */
	private DateValueCorrector dateValueCorrector = new DateValueCorrector();
	
	
	public DefaultSqlResolver() {
	}

	public DefaultSqlResolver(Dialect dialect, DateValueCorrector dateValueCorrector) {
		super(dialect);
		this.dateValueCorrector = dateValueCorrector;
	}

	@Override
	public <T> SearchSql<T> resolve(BeanMeta<T> beanMeta, SearchParam searchParam) {
		List<String> fetchFields = searchParam.getFetchFields();
		SearchSql<T> searchSql = new SearchSql<>(beanMeta, fetchFields);

		FetchType fetchType = searchParam.getFetchType();
		searchSql.setShouldQueryCluster(fetchType.shouldQueryCluster());
		searchSql.setShouldQueryList(fetchType.shouldQueryList());

		String fieldSelectSql = buildFieldSelectSql(beanMeta, searchParam, fetchFields, searchSql);
		String fromWhereSql = buildFromWhereSql(beanMeta, searchParam, searchSql);

		String groupBy = beanMeta.getGroupBy();
		String[] summaryFields = fetchType.getSummaryFields();
		boolean shouldQueryTotal = fetchType.shouldQueryTotal();
		if (StringUtils.isBlank(groupBy)) {
			if (shouldQueryTotal || summaryFields.length > 0) {
				if (beanMeta.isDistinct()) {
					String originalSql = fieldSelectSql + fromWhereSql;
					String clusterSelectSql = resolveClusterSelectSql(searchSql, summaryFields, shouldQueryTotal, originalSql);
					String tableAlias = generateTableAlias(originalSql);
					searchSql.setClusterSqlString(clusterSelectSql + " from (" + originalSql + ") " + tableAlias);
				} else {
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
			fromWhereSql = fromWhereSql + " group by " + groupBy;
			if (shouldQueryTotal || summaryFields.length > 0) {
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
			PaginateSql paginateSql = buildPaginateSql(beanMeta, searchParam, fromWhereSql, fieldSelectSql);
			searchSql.setListSqlString(paginateSql.getSql());
			searchSql.addListSqlParams(paginateSql.getParams());
		}
		return searchSql;
	}

	protected <T> String buildFieldSelectSql(BeanMeta<T> beanMeta, SearchParam searchParam, List<String> fetchFields, SearchSql<T> searchSql) {
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
		return builder.toString();
	}

	protected <T> String buildFromWhereSql(BeanMeta<T> beanMeta, SearchParam searchParam, SearchSql<T> searchSql) {
		String tables = resolveTables(beanMeta.getTableSnippet(), searchParam, searchSql);
		StringBuilder builder = new StringBuilder(" from ").append(tables);
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
			FieldParam param = fieldParamList.get(i);
			// 这里没取字段别名，因为在 count SQL 里，select 语句中可能没这个字段
			FieldMeta meta = beanMeta.requireFieldMeta(param.getName());
			List<Object> sqlParams = appendCondition(builder, meta, param);
			for (Object sqlParam : sqlParams) {
				searchSql.addListSqlParam(sqlParam);
				searchSql.addClusterSqlParam(sqlParam);
			}
			builder.append(")");
		}
		return builder.toString();
	}

	protected <T> PaginateSql buildPaginateSql(BeanMeta<T> beanMeta, SearchParam searchParam, String fromWhereSql, String fieldSelectSql) {
		OrderBy orderBy = searchParam.getOrderBy();
		if (orderBy != null) {
			FieldMeta meta = beanMeta.requireFieldMeta(orderBy.getSort());
			fromWhereSql = fromWhereSql + " order by " + meta.getDbAlias();
			String order = orderBy.getOrder();
			if (order != null) {
				fromWhereSql = fromWhereSql + " " + order;
			}
		}
		return forPaginate(fieldSelectSql, fromWhereSql, searchParam.getPaging());
	}

	protected <T> String resolveTables(SqlSnippet tableSnippet, SearchParam searchParam, SearchSql<T> searchSql) {
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

	protected <T> String resolveDbField(SqlSnippet dbFieldSnippet, SearchParam searchParam, SearchSql<T> searchSql, boolean distinct) {
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


	protected <T> String resolveClusterSelectSql(SearchSql<T> searchSql, String[] summaryFields,
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

	protected String generateTableAlias(String originalSql) {
		return generateAlias("t_", originalSql);
	}

	protected String generateColumnAlias(String seed, String originalSql) {
		// 注意：Oracle 数据库的别名不能以下划线开头
		return generateAlias("s_" + seed, originalSql);
	}

	protected String generateAlias(String seed, String originalSql) {
		int index = 0;
		String tableAlias = seed;
		while (originalSql.contains(tableAlias)) {
			tableAlias = seed + index++;
		}
		return tableAlias;
	}

	protected List<Object> appendCondition(StringBuilder builder, FieldMeta fieldMeta, FieldParam param) {
		Object[] values = param.getValues();
		FieldOp operator = (FieldOp) param.getOperator();
		if (dateValueCorrector != null) {
			values = dateValueCorrector.correct(fieldMeta.getType(), values, operator);
		}
		FieldOp.OpPara opPara = new FieldOp.OpPara(fieldMeta, param.isIgnoreCase(), values);
		return operator.operate(builder, opPara);
	}

	public DateValueCorrector getDateValueCorrector() {
		return dateValueCorrector;
	}

	public void setDateValueCorrector(DateValueCorrector dateValueCorrector) {
		this.dateValueCorrector = Objects.requireNonNull(dateValueCorrector);
	}

}
