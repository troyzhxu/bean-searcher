package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.dialect.Dialect;
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

		SqlWrapper<Object> fieldSelectSqlWrapper = buildFieldSelectSql(beanMeta, searchParam, fetchFields);
		searchSql.addListSqlParams(fieldSelectSqlWrapper.getParas());
		// 只有在 distinct 条件，聚族查询 SQL 里才会出现 字段查询 语句，才需要将 内嵌参数放到 聚族参数里
		if (beanMeta.isDistinct()) {
			searchSql.addClusterSqlParams(fieldSelectSqlWrapper.getParas());
		}
		String fieldSelectSql = fieldSelectSqlWrapper.getSql();

		SqlWrapper<Object> fromWhereSqlWrapper = buildFromWhereSql(beanMeta, searchParam);
		searchSql.addListSqlParams(fromWhereSqlWrapper.getParas());
		searchSql.addClusterSqlParams(fromWhereSqlWrapper.getParas());
		String fromWhereSql = fromWhereSqlWrapper.getSql();

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
			List<SqlSnippet.SqlPara> groupParams = beanMeta.getGroupBySqlParas();
			if (groupParams != null) {
				for (SqlSnippet.SqlPara param : groupParams) {
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
			SqlWrapper<Object> listSql = buildListSql(beanMeta, searchParam, fromWhereSql, fieldSelectSql);
			searchSql.setListSqlString(listSql.getSql());
			searchSql.addListSqlParams(listSql.getParas());
		}
		return searchSql;
	}

	protected <T> SqlWrapper<Object> buildFieldSelectSql(BeanMeta<T> beanMeta, SearchParam searchParam, List<String> fetchFields) {
		SqlWrapper<Object> sqlWrapper = new SqlWrapper<>();
		StringBuilder builder = new StringBuilder("select ");
		if (beanMeta.isDistinct()) {
			builder.append("distinct ");
		}
		int fieldCount = fetchFields.size();
		for (int i = 0; i < fieldCount; i++) {
			String field = fetchFields.get(i);
			FieldMeta meta = beanMeta.requireFieldMeta(field);
			SqlWrapper<Object> dbFieldSql = resolveDbFieldSql(meta.getFieldSql(), searchParam);
			builder.append(dbFieldSql.getSql()).append(" ").append(meta.getDbAlias());
			if (i < fieldCount - 1) {
				builder.append(", ");
			}
			sqlWrapper.addParas(dbFieldSql.getParas());
		}
		sqlWrapper.setSql(builder.toString());
		return sqlWrapper;
	}

	protected <T> SqlWrapper<Object> buildFromWhereSql(BeanMeta<T> beanMeta, SearchParam searchParam) {
		SqlWrapper<Object> sqlWrapper = new SqlWrapper<>();
		SqlWrapper<Object> tableSql = resolveTableSql(beanMeta.getTableSnippet(), searchParam);
		sqlWrapper.addParas(tableSql.getParas());
		StringBuilder builder = new StringBuilder(" from ").append(tableSql.getSql());
		String joinCond = beanMeta.getJoinCond();
		boolean hasJoinCond = StringUtils.isNotBlank(joinCond);
		List<FieldParam> fieldParamList = searchParam.getFieldParams();
		if (hasJoinCond || fieldParamList.size() > 0) {
			builder.append(" where (");
			if (hasJoinCond) {
				List<SqlSnippet.SqlPara> joinCondParams = beanMeta.getJoinCondSqlParas();
				for (SqlSnippet.SqlPara param : joinCondParams) {
					Object sqlParam = searchParam.getPara(param.getName());
					if (param.isJdbcPara()) {
						sqlWrapper.addPara(sqlParam);
					} else {
						String strParam = sqlParam != null ? sqlParam.toString() : "";
						joinCond = joinCond.replace(param.getSqlName(), strParam);
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
			sqlWrapper.addParas(appendCondition(builder, meta, param));
			builder.append(")");
		}
		sqlWrapper.setSql(builder.toString());
		return sqlWrapper;
	}

	protected <T> SqlWrapper<Object> buildListSql(BeanMeta<T> beanMeta, SearchParam searchParam, String fromWhereSql, String fieldSelectSql) {
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

	protected SqlWrapper<Object> resolveTableSql(SqlSnippet tableSnippet, SearchParam searchParam) {
		SqlWrapper<Object> sqlWrapper = new SqlWrapper<>();
		String tables = tableSnippet.getSql();
		List<SqlSnippet.SqlPara> params = tableSnippet.getParas();
		for (SqlSnippet.SqlPara param : params) {
			Object sqlParam = searchParam.getPara(param.getName());
			if (param.isJdbcPara()) {
				sqlWrapper.addPara(sqlParam);
			} else {
				String strParam = sqlParam != null ? sqlParam.toString() : "";
				tables = tables.replace(param.getSqlName(), strParam);
			}
		}
		sqlWrapper.setSql(tables);
		return sqlWrapper;
	}

	protected SqlWrapper<Object> resolveDbFieldSql(SqlSnippet dbFieldSnippet, SearchParam searchParam) {
		String dbField = dbFieldSnippet.getSql();
		List<SqlSnippet.SqlPara> params = dbFieldSnippet.getParas();
		SqlWrapper<Object> sqlWrapper = new SqlWrapper<>();
		for (SqlSnippet.SqlPara param : params) {
			Object sqlParam = searchParam.getPara(param.getName());
			if (param.isJdbcPara()) {
				sqlWrapper.addPara(sqlParam);
			} else {
				String strParam = sqlParam != null ? sqlParam.toString() : "";
				dbField = dbField.replace(param.getSqlName(), strParam);
			}
		}
		sqlWrapper.setSql(dbField);
		return sqlWrapper;
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
