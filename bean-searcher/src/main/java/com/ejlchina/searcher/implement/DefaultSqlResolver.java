package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.dialect.Dialect;
import com.ejlchina.searcher.param.FetchType;
import com.ejlchina.searcher.param.FieldParam;
import com.ejlchina.searcher.param.OrderBy;
import com.ejlchina.searcher.param.Paging;
import com.ejlchina.searcher.group.Group;
import com.ejlchina.searcher.util.StringUtils;

import java.util.List;
import java.util.Map;
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
		FetchType fetchType = searchParam.getFetchType();

		SearchSql<T> searchSql = new SearchSql<>(beanMeta, fetchFields);
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
		Map<String, Object> paraMap = searchParam.getParaMap();

		SqlWrapper<Object> fieldSelectSqlWrapper = buildFieldSelectSql(beanMeta, fetchFields, paraMap);
		SqlWrapper<Object> fromWhereSqlWrapper = buildFromWhereSql(beanMeta, searchParam.getFieldParamGroup(), paraMap);
		String fieldSelectSql = fieldSelectSqlWrapper.getSql();
		String fromWhereSql = fromWhereSqlWrapper.getSql();

		if (fetchType.shouldQueryTotal() || summaryFields.length > 0) {
			List<String> summaryAliases = searchSql.getSummaryAliases();
			String countAlias = searchSql.getCountAlias();
			SqlWrapper<Object> clusterSelectSql = buildClusterSelectSql(beanMeta, summaryFields, summaryAliases, countAlias, paraMap);
			String clusterSql = buildClusterSql(beanMeta, clusterSelectSql.getSql(), fieldSelectSql, fromWhereSql);
			searchSql.setClusterSqlString(clusterSql);
			searchSql.addClusterSqlParams(clusterSelectSql.getParas());
			// 只有在 distinct 条件下，聚族查询 SQL 里才会出现 字段查询 语句，才需要将 字段内嵌参数放到 聚族参数里
			if (beanMeta.isDistinct()) {
				searchSql.addClusterSqlParams(fieldSelectSqlWrapper.getParas());
			}
			searchSql.addClusterSqlParams(fromWhereSqlWrapper.getParas());
		}
		if (fetchType.shouldQueryList()) {
			List<OrderBy> orderBys = searchParam.getOrderBys();
			Paging paging = searchParam.getPaging();
			SqlWrapper<Object> listSql = buildListSql(beanMeta, fieldSelectSql, fromWhereSql, orderBys, paging, fetchFields, paraMap);
			searchSql.setListSqlString(listSql.getSql());
			searchSql.addListSqlParams(fieldSelectSqlWrapper.getParas());
			searchSql.addListSqlParams(fromWhereSqlWrapper.getParas());
			searchSql.addListSqlParams(listSql.getParas());
		}
		return searchSql;
	}

	protected <T> SqlWrapper<Object> buildFieldSelectSql(BeanMeta<T> beanMeta, List<String> fetchFields, Map<String, Object> paraMap) {
		SqlWrapper<Object> sqlWrapper = new SqlWrapper<>();
		StringBuilder builder = new StringBuilder("select ");
		if (beanMeta.isDistinct()) {
			builder.append("distinct ");
		}
		int fieldCount = fetchFields.size();
		for (int i = 0; i < fieldCount; i++) {
			String field = fetchFields.get(i);
			FieldMeta meta = beanMeta.requireFieldMeta(field);
			SqlWrapper<Object> dbFieldSql = resolveDbFieldSql(meta.getFieldSql(), paraMap);
			builder.append(dbFieldSql.getSql()).append(" ").append(meta.getDbAlias());
			if (i < fieldCount - 1) {
				builder.append(", ");
			}
			sqlWrapper.addParas(dbFieldSql.getParas());
		}
		sqlWrapper.setSql(builder.toString());
		return sqlWrapper;
	}

	protected <T> SqlWrapper<Object> buildClusterSelectSql(BeanMeta<T> beanMeta, String[] summaryFields, List<String> summaryAliases, String countAlias, Map<String, Object> paraMap) {
		StringBuilder builder = new StringBuilder("select ");
		if (countAlias != null) {
			builder.append("count(*) ").append(countAlias);
			if (summaryFields.length > 0) {
				builder.append(", ");
			}
		}
		SqlWrapper<Object> sqlWrapper = new SqlWrapper<>();
		boolean distinctOrGroupBy = beanMeta.isDistinctOrGroupBy();
		for (int i = 0; i < summaryFields.length; i++) {
			FieldMeta fieldMeta = beanMeta.requireFieldMeta(summaryFields[i]);
			builder.append("sum(");
			if (distinctOrGroupBy) {
				builder.append(fieldMeta.getDbAlias());
			} else {
				SqlWrapper<Object> fieldSql = resolveDbFieldSql(fieldMeta.getFieldSql(), paraMap);
				builder.append(fieldSql.getSql());
				sqlWrapper.addParas(fieldSql.getParas());
			}
			builder.append(") ").append(summaryAliases.get(i));
			if (i < summaryFields.length - 1) {
				builder.append(", ");
			}
		}
		sqlWrapper.setSql(builder.toString());
		return sqlWrapper;
	}

	protected <T> SqlWrapper<Object> buildFromWhereSql(BeanMeta<T> beanMeta, Group<List<FieldParam>> fieldParamGroup, Map<String, Object> paraMap) {
		SqlWrapper<Object> sqlWrapper = new SqlWrapper<>();
		SqlWrapper<Object> tableSql = resolveTableSql(beanMeta.getTableSnippet(), paraMap);
		sqlWrapper.addParas(tableSql.getParas());
		StringBuilder builder = new StringBuilder(" from ").append(tableSql.getSql());
		String joinCond = beanMeta.getJoinCond();
		if (StringUtils.isNotBlank(joinCond)) {
			List<SqlSnippet.SqlPara> joinCondParams = beanMeta.getJoinCondSqlParas();
			for (SqlSnippet.SqlPara param : joinCondParams) {
				Object sqlParam = paraMap.get(param.getName());
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
		boolean hasFieldParams = fieldParamGroup.judgeAny(l -> l.size() > 0);

		if (hasJoinCond || hasFieldParams) {
			builder.append(" where ");
			if (hasJoinCond) {
				builder.append("(").append(joinCond).append(")");
				if (hasFieldParams) {
					builder.append(" and ");
				}
			}
		}
		fieldParamGroup.forEach(event -> {
			if (event.isGroupStart()) {
				builder.append("(");
			} else
			if (event.isGroupEnd()) {
				builder.append(")");
			} else
			if (event.isGroupAnd()) {
				builder.append(" and ");
			} else
			if (event.isGroupOr()) {
				builder.append(" or ");
			} else {
				List<FieldParam> params = event.getValue();
				for (int i = 0; i < params.size(); i++) {
					if (i == 0) {
						builder.append("(");
					} else {
						builder.append(" and (");
					}
					FieldParam param = params.get(i);
					FieldMeta fieldMeta = beanMeta.requireFieldMeta(param.getName());
					Object[] values = param.getValues();
					FieldOp operator = (FieldOp) param.getOperator();
					if (dateValueCorrector != null) {
						values = dateValueCorrector.correct(fieldMeta.getType(), values, operator);
					}
					SqlWrapper<Object> fieldSql = resolveDbFieldSql(fieldMeta.getFieldSql(), paraMap);
					FieldOp.OpPara opPara = new FieldOp.OpPara(fieldSql, param.isIgnoreCase(), values);
					sqlWrapper.addParas(operator.operate(builder, opPara));
					builder.append(")");
				}
			}
		});
		String groupBy = beanMeta.getGroupBy();
		if (StringUtils.isNotBlank(groupBy)) {
			List<SqlSnippet.SqlPara> groupParams = beanMeta.getGroupBySqlParas();
			if (groupParams != null) {
				for (SqlSnippet.SqlPara param : groupParams) {
					Object sqlParam = paraMap.get(param.getName());
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

	protected <T> String buildClusterSql(BeanMeta<T> beanMeta, String clusterSelectSql, String fieldSelectSql, String fromWhereSql) {
		if (beanMeta.isDistinctOrGroupBy()) {
			String tableAlias = getTableAlias(beanMeta);
			return clusterSelectSql + " from (" + fieldSelectSql + fromWhereSql + ") " + tableAlias;
		}
		return clusterSelectSql + fromWhereSql;
	}

	protected <T> SqlWrapper<Object> buildListSql(BeanMeta<T> beanMeta, String fieldSelectSql, String fromWhereSql,
				List<OrderBy> orderBys, Paging paging, List<String> fetchFields, Map<String, Object> paraMap) {
		SqlSnippet orderBySnippet = beanMeta.getOrderBySnippet();
		boolean defaultOrderBy = StringUtils.isNotBlank(orderBySnippet.getSql());
		StringBuilder builder = new StringBuilder(fromWhereSql);
		int count = orderBys.size();
		if (count > 0 || defaultOrderBy) {
			builder.append(" order by ");
		}
		for (int index = 0; index < count; index++) {
			OrderBy orderBy = orderBys.get(index);
			FieldMeta meta = beanMeta.requireFieldMeta(orderBy.getSort());
			if (fetchFields.contains(meta.getName())) {
				builder.append(meta.getDbAlias());
			} else {
				builder.append(meta.getFieldSql().getSql());
			}
			String order = orderBy.getOrder();
			if (StringUtils.isNotBlank(order)) {
				builder.append(' ').append(order);
			}
			if (index < count - 1) {
				builder.append(", ");
			}
		}
		if (count == 0 && defaultOrderBy) {
			SqlWrapper<Object> dbFieldSql = resolveDbFieldSql(orderBySnippet, paraMap);
			builder.append(dbFieldSql.getSql());
			SqlWrapper<Object> sqlWrapper = forPaginate(fieldSelectSql, builder.toString(), paging);
			SqlWrapper<Object> listSql = new SqlWrapper<>(sqlWrapper.getSql());
			listSql.addParas(dbFieldSql.getParas());
			listSql.addParas(sqlWrapper.getParas());
			return listSql;
		}
		return forPaginate(fieldSelectSql, builder.toString(), paging);
	}

	protected SqlWrapper<Object> resolveTableSql(SqlSnippet tableSnippet, Map<String, Object> paraMap) {
		SqlWrapper<Object> sqlWrapper = new SqlWrapper<>();
		String tables = tableSnippet.getSql();
		List<SqlSnippet.SqlPara> params = tableSnippet.getParas();
		for (SqlSnippet.SqlPara param : params) {
			Object sqlParam = paraMap.get(param.getName());
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

	protected SqlWrapper<Object> resolveDbFieldSql(SqlSnippet dbFieldSnippet, Map<String, Object> paraMap) {
		String dbField = dbFieldSnippet.getSql();
		List<SqlSnippet.SqlPara> params = dbFieldSnippet.getParas();
		SqlWrapper<Object> sqlWrapper = new SqlWrapper<>();
		for (SqlSnippet.SqlPara param : params) {
			Object sqlParam = paraMap.get(param.getName());
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

	public DateValueCorrector getDateValueCorrector() {
		return dateValueCorrector;
	}

	public void setDateValueCorrector(DateValueCorrector dateValueCorrector) {
		this.dateValueCorrector = Objects.requireNonNull(dateValueCorrector);
	}

}
