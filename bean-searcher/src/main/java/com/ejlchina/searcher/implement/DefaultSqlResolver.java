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

		if (fetchType.shouldQueryTotal()) {
			searchSql.setCountAlias(getCountAlias(beanMeta));
		}
		String[] summaryFields = fetchType.getSummaryFields();
		for (String summaryField : summaryFields) {
			FieldMeta fieldMeta = beanMeta.getFieldMeta(summaryField);
			if (fieldMeta == null) {
				throw new SearchException("求和属性【" + summaryField + "】没有和数据库字段做映射，请检查该属性是否 已被忽略 或 是否已被 @DbField 正确注解！");
			}
			searchSql.addSummaryAlias(getSummaryAlias(fieldMeta));
		}
		SqlWrapper<Object> fieldSelectSqlWrapper = buildFieldSelectSql(beanMeta, searchParam, fetchFields);
		String fieldSelectSql = fieldSelectSqlWrapper.getSql();
		if (fetchType.shouldQueryTotal() || summaryFields.length > 0) {
			SqlWrapper<Object> fromWhereSqlWrapper = buildFromWhereSql(beanMeta, searchParam, beanMeta.isDistinct());
			String fromWhereSql = fromWhereSqlWrapper.getSql();
			List<String> summaryAliases = searchSql.getSummaryAliases();
			String countAlias = searchSql.getCountAlias();
			String clusterSql = buildClusterSql(beanMeta, summaryFields, summaryAliases, countAlias, fieldSelectSql, fromWhereSql);
			searchSql.setClusterSqlString(clusterSql);
			// 只有在 distinct 条件下，聚族查询 SQL 里才会出现 字段查询 语句，才需要将 字段内嵌参数放到 聚族参数里
			if (beanMeta.isDistinct()) {
				searchSql.addClusterSqlParams(fieldSelectSqlWrapper.getParas());
			}
			searchSql.addClusterSqlParams(fromWhereSqlWrapper.getParas());
		}
		if (fetchType.shouldQueryList()) {
			SqlWrapper<Object> fromWhereSqlWrapper = buildFromWhereSql(beanMeta, searchParam, true);
			String fromWhereSql = fromWhereSqlWrapper.getSql();
			SqlWrapper<Object> listSql = buildListSql(beanMeta, searchParam, fieldSelectSql, fromWhereSql);
			searchSql.setListSqlString(listSql.getSql());
			searchSql.addListSqlParams(fieldSelectSqlWrapper.getParas());
			searchSql.addListSqlParams(fromWhereSqlWrapper.getParas());
			searchSql.addListSqlParams(listSql.getParas());
		}
		return searchSql;
	}

	private <T> String buildClusterSql(BeanMeta<T> beanMeta, String[] summaryFields, List<String> summaryAliases, String countAlias, String fieldSelectSql, String fromWhereSql) {
		if (beanMeta.isDistinct()) {
			String clusterSelectSql = resolveClusterSelectSql(beanMeta, summaryFields, summaryAliases, countAlias);
			String originalSql = fieldSelectSql + fromWhereSql;
			String tableAlias = getTableAlias(beanMeta);
			return clusterSelectSql + " from (" + originalSql + ") " + tableAlias;
		}
		String clusterSelectSql = resolveClusterSelectSql(beanMeta, summaryFields, summaryAliases, countAlias);
		if (StringUtils.isBlank(beanMeta.getGroupBy())) {
			return clusterSelectSql + fromWhereSql;
		}
		String tableAlias = getTableAlias(beanMeta);
		return clusterSelectSql + " from (select count(*) " + fromWhereSql + ") " + tableAlias;
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

	protected <T> SqlWrapper<Object> buildFromWhereSql(BeanMeta<T> beanMeta, SearchParam searchParam, boolean canUseAlias) {
		SqlWrapper<Object> sqlWrapper = new SqlWrapper<>();
		SqlWrapper<Object> tableSql = resolveTableSql(beanMeta.getTableSnippet(), searchParam);
		sqlWrapper.addParas(tableSql.getParas());
		StringBuilder builder = new StringBuilder(" from ").append(tableSql.getSql());
		String joinCond = beanMeta.getJoinCond();
		if (StringUtils.isNotBlank(joinCond)) {
			List<SqlSnippet.SqlPara> joinCondParams = beanMeta.getJoinCondSqlParas();
			for (SqlSnippet.SqlPara param : joinCondParams) {
				Object sqlParam = searchParam.getPara(param.getName());
				if (param.isJdbcPara()) {
					sqlWrapper.addPara(sqlParam);
				} else {
					// 将这部分逻辑提上来，当 joinCond 只有一个拼接参数 且 该参数为空时，使其不参与 where 子句
					String strParam = sqlParam != null ? sqlParam.toString() : "";
					joinCond = joinCond.replace(param.getSqlName(), strParam);
				}
			}
		}
		boolean hasJoinCond = StringUtils.isNotBlank(joinCond);
		List<FieldParam> fieldParamList = searchParam.getFieldParams();
		if (hasJoinCond || fieldParamList.size() > 0) {
			builder.append(" where (");
			if (hasJoinCond) {
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
		String groupBy = beanMeta.getGroupBy();
		if (StringUtils.isNotBlank(groupBy)) {
			List<SqlSnippet.SqlPara> groupParams = beanMeta.getGroupBySqlParas();
			if (groupParams != null) {
				for (SqlSnippet.SqlPara param : groupParams) {
					Object sqlParam = searchParam.getPara(param.getName());
					if (param.isJdbcPara()) {
						sqlWrapper.addPara(sqlParam);
					} else {
						String strParam = sqlParam != null ? sqlParam.toString() : "";
						groupBy = groupBy.replace(param.getSqlName(), strParam);
					}
				}
			}
			builder.append(" group by ").append(groupBy);
		}
		sqlWrapper.setSql(builder.toString());
		return sqlWrapper;
	}

	protected <T> SqlWrapper<Object> buildListSql(BeanMeta<T> beanMeta, SearchParam searchParam, String fieldSelectSql, String fromWhereSql) {
		StringBuilder builder = new StringBuilder(fromWhereSql);
		List<OrderBy> orderBys = searchParam.getOrderBys();
		int count = orderBys.size();
		if (count > 0) {
			builder.append(" order by ");
		}
		for (int index = 0; index < count; index++) {
			OrderBy orderBy = orderBys.get(index);
			FieldMeta meta = beanMeta.requireFieldMeta(orderBy.getSort());
			builder.append(meta.getDbAlias());
			String order = orderBy.getOrder();
			if (StringUtils.isNotBlank(order)) {
				builder.append(' ').append(order);
			}
			if (index < count - 1) {
				builder.append(", ");
			}
		}
		return forPaginate(fieldSelectSql, builder.toString(), searchParam.getPaging());
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


	protected <T> String resolveClusterSelectSql(BeanMeta<T> beanMeta, String[] summaryFields, List<String> summaryAliases, String countAlias) {
		StringBuilder builder = new StringBuilder("select ");
		if (countAlias != null) {
			builder.append("count(*) ").append(countAlias);
			if (summaryFields.length > 0) {
				builder.append(", ");
			}
		}
		for (int i = 0; i < summaryFields.length; i++) {
			builder.append("sum(")
					.append(beanMeta.getFieldSql(summaryFields[i]))
					.append(") ")
					.append(summaryAliases.get(i));
			if (i < summaryFields.length - 1) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}

	protected <T> String getCountAlias(BeanMeta<T> beanMeta) {
		// 注意：Oracle 数据库的别名不能以下划线开头，留参 beanMeta 方便用户重写该方法
		return "s_count";
	}

	protected String getSummaryAlias(FieldMeta fieldMeta) {
		// 注意：Oracle 数据库的别名不能以下划线开头，留参 fieldMeta 方便用户重写该方法
		return fieldMeta.getDbAlias() + "_sum_";
	}

	protected <T> String getTableAlias(BeanMeta<T> beanMeta) {
		// 注意：Oracle 数据库的别名不能以下划线开头，留参 beanMeta 方便用户重写该方法
		return "t_";
	}

	protected List<Object> appendCondition(StringBuilder builder, FieldMeta fieldMeta, FieldParam param) {
		Object[] values = param.getValues();
		FieldOp operator = (FieldOp) param.getOperator();
		if (dateValueCorrector != null) {
			values = dateValueCorrector.correct(fieldMeta.getType(), values, operator);
		}
		FieldOp.OpPara opPara = new FieldOp.OpPara(fieldMeta.getFieldSql(), param.isIgnoreCase(), values);
		return operator.operate(builder, opPara);
	}

	public DateValueCorrector getDateValueCorrector() {
		return dateValueCorrector;
	}

	public void setDateValueCorrector(DateValueCorrector dateValueCorrector) {
		this.dateValueCorrector = Objects.requireNonNull(dateValueCorrector);
	}

}
