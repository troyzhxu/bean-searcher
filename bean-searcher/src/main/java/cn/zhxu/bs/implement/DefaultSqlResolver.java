package cn.zhxu.bs.implement;

import cn.zhxu.bs.*;
import cn.zhxu.bs.dialect.Dialect;
import cn.zhxu.bs.dialect.DialectWrapper;
import cn.zhxu.bs.group.Group;
import cn.zhxu.bs.group.GroupPair;
import cn.zhxu.bs.param.FetchType;
import cn.zhxu.bs.param.FieldParam;
import cn.zhxu.bs.param.OrderBy;
import cn.zhxu.bs.param.Paging;
import cn.zhxu.bs.util.StringUtils;

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

	private GroupPair.Resolver groupPairResolver = new GroupPairResolver();

	public DefaultSqlResolver() { }

	public DefaultSqlResolver(Dialect dialect) {
		super(dialect);
	}

	@Override
	public <T> SearchSql<T> resolve(BeanMeta<T> beanMeta, SearchParam searchParam) {
		FetchType fetchType = searchParam.getFetchType();
		SearchSql<T> searchSql = new SearchSql<>(beanMeta, searchParam);
		searchSql.setShouldQueryCluster(fetchType.shouldQueryCluster());
		searchSql.setShouldQueryList(fetchType.shouldQueryList());

		if (fetchType.shouldQueryTotal()) {
			searchSql.setCountAlias(getCountAlias(beanMeta));
		}
		String[] summaryFields = fetchType.getSummaryFields();
		for (String summaryField : summaryFields) {
			FieldMeta fieldMeta = beanMeta.getFieldMeta(summaryField);
			if (fieldMeta == null) {
				throw new SearchException("No such field [" + summaryField + "] on " + beanMeta.getBeanClass() + " for summary.");
			}
			searchSql.addSummaryAlias(getSummaryAlias(fieldMeta));
		}
		List<String> fetchFields = searchParam.getFetchFields();
		Map<String, Object> paraMap = searchParam.getParaMap();

		SqlWrapper<Object> fieldSelectSqlWrapper = buildFieldSelectSql(beanMeta, fetchFields, paraMap);
		SqlWrapper<Object> fromWhereSqlWrapper = buildFromWhereSql(beanMeta, searchParam);
		String fieldSelectSql = fieldSelectSqlWrapper.getSql();
		String fromWhereSql = fromWhereSqlWrapper.getSql();

		if (fetchType.shouldQueryTotal() || summaryFields.length > 0) {
			List<String> summaryAliases = searchSql.getSummaryAliases();
			String countAlias = searchSql.getCountAlias();
			SqlWrapper<Object> clusterSelectSql = buildClusterSelectSql(beanMeta, summaryFields, summaryAliases, countAlias, paraMap);
			String clusterSql = buildClusterSql(beanMeta, clusterSelectSql.getSql(), fieldSelectSql, fromWhereSql);
			searchSql.setClusterSqlString(clusterSql);
			searchSql.addClusterSqlParams(clusterSelectSql.getParas());
			// 只有在 DistinctOrGroupBy 条件下，聚族查询 SQL 里才会出现 字段查询 语句，才需要将 字段内嵌参数放到 聚族参数里
			if (beanMeta.isDistinctOrGroupBy()) {
				searchSql.addClusterSqlParams(fieldSelectSqlWrapper.getParas());
			}
			searchSql.addClusterSqlParams(fromWhereSqlWrapper.getParas());
		}
		if (fetchType.shouldQueryList()) {
			Paging paging = searchParam.getPaging();
			// 如果分页 size <= 0，则不生成 listSql
			if (paging == null || paging.getSize() > 0) {
				List<OrderBy> orderBys = searchParam.getOrderBys();
				SqlWrapper<Object> listSql = buildListSql(beanMeta, fieldSelectSql, fromWhereSql, orderBys, paging, fetchFields, paraMap);
				searchSql.setListSqlString(listSql.getSql());
				searchSql.addListSqlParams(fieldSelectSqlWrapper.getParas());
				searchSql.addListSqlParams(fromWhereSqlWrapper.getParas());
				searchSql.addListSqlParams(listSql.getParas());
			}
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

	protected SqlWrapper<Object> buildFromWhereSql(BeanMeta<?> beanMeta, SearchParam searchParam) {
		Group<List<FieldParam>> paramsGroup = searchParam.getParamsGroup();
		List<String> fetchFields = searchParam.getFetchFields();
		Map<String, Object> paraMap = searchParam.getParaMap();

		SqlWrapper<Object> sqlWrapper = new SqlWrapper<>();
		SqlWrapper<Object> tableSql = resolveTableSql(beanMeta.getTableSnippet(), paraMap);
		sqlWrapper.addParas(tableSql.getParas());
		StringBuilder builder = new StringBuilder(" from ").append(tableSql.getSql());

		String where = beanMeta.getWhere();
		if (StringUtils.isNotBlank(where)) {
			where = buildSqlSnippet(where, beanMeta.getWhereSqlParas(), paraMap, sqlWrapper.getParas());
		}
		SqlWrapper<Object> groupBy = resolveGroupBy(beanMeta, paraMap);
		GroupPair groupPair = resolveGroupPair(beanMeta, paramsGroup, groupBy);
		Group<List<FieldParam>> whereGroup = groupPair.getWhereGroup();

		boolean hasWhere = StringUtils.isNotBlank(where);
		boolean hasWhereParams = whereGroup.judgeAny(l -> !l.isEmpty());

		if (hasWhere || hasWhereParams) {
			builder.append(" where ");
			if (hasWhere) {
				builder.append("(").append(where).append(")");
				if (hasWhereParams) {
					builder.append(" and ");
				}
			}
			if (hasWhereParams) {
				useGroup(whereGroup, beanMeta, fetchFields, paraMap, builder, sqlWrapper.getParas(), false);
			}
		}
		if (groupBy != null) {
			builder.append(" group by ").append(groupBy.getSql());
			sqlWrapper.addParas(groupBy.getParas());
			String having = beanMeta.getHaving();
			if (StringUtils.isNotBlank(having)) {
				having = buildSqlSnippet(having, beanMeta.getHavingSqlParas(), paraMap, sqlWrapper.getParas());
			}
			Group<List<FieldParam>> havingGroup = groupPair.getHavingGroup();
			boolean hasHaving = StringUtils.isNotBlank(having);
			boolean hasHavingParams = havingGroup.judgeAny(l -> !l.isEmpty());
			if (hasHaving || hasHavingParams) {
				builder.append(" having ");
				if (hasHaving) {
					builder.append("(").append(having).append(")");
					if (hasHavingParams) {
						builder.append(" and ");
					}
				}
				if (hasHavingParams) {
					useGroup(havingGroup, beanMeta, fetchFields, paraMap, builder, sqlWrapper.getParas(), true);
				}
			}
		}
		sqlWrapper.setSql(builder.toString());
		return sqlWrapper;
	}

	protected void useGroup(Group<List<FieldParam>> group, BeanMeta<?> beanMeta, List<String> fetchFields, Map<String, Object> paraMap,
							StringBuilder sqlBuilder, List<Object> paraReceiver, boolean isHaving) {
		group.forEach(event -> {
			if (event.isGroupStart()) {
				sqlBuilder.append("(");
				return;
			}
			if (event.isGroupEnd()) {
				sqlBuilder.append(")");
				return;
			}
			if (event.isGroupAnd()) {
				sqlBuilder.append(" and ");
				return;
			}
			if (event.isGroupOr()) {
				sqlBuilder.append(" or ");
				return;
			}
			List<FieldParam> params = event.getValue();
			for (int i = 0; i < params.size(); i++) {
				if (i == 0) {
					sqlBuilder.append("(");
				} else {
					sqlBuilder.append(" and (");
				}
				FieldParam param = params.get(i);
				FieldOp.OpPara opPara = new FieldOp.OpPara(
						(name) -> {
							String field = name != null ? name : param.getName();
							FieldMeta meta = beanMeta.requireFieldMeta(field);
							SqlSnippet sql = meta.getFieldSql();
							//如果是 group by having 且 Select 列表中 存在该字段，则使用该字段的别名
							if (isHaving && fetchFields.contains(field)) {
								sql = new SqlSnippet(meta.getDbAlias());
							}
							return resolveDbFieldSql(sql, paraMap);
						},
						param.isIgnoreCase(),
						param.getValues()
				);
				FieldOp operator = (FieldOp) param.getOperator();
				paraReceiver.addAll(operator.operate(sqlBuilder, opPara));
				sqlBuilder.append(")");
			}
		});
	}

	protected SqlWrapper<Object> resolveGroupBy(BeanMeta<?> beanMeta, Map<String, Object> paraMap) {
		String groupBy = beanMeta.getGroupBy();
		if (StringUtils.isNotBlank(groupBy)) {
			SqlWrapper<Object> sqlWrapper = new SqlWrapper<>();
			groupBy = buildSqlSnippet(groupBy, beanMeta.getGroupBySqlParas(), paraMap, sqlWrapper.getParas());
			if (StringUtils.isNotBlank(groupBy)) {
				sqlWrapper.setSql(groupBy);
				return sqlWrapper;
			}
		}
		return null;
	}

	protected GroupPair resolveGroupPair(BeanMeta<?> beanMeta, Group<List<FieldParam>> paramsGroup, SqlWrapper<Object> groupBy) {
		if (groupBy == null) {
			return new GroupPair(paramsGroup, GroupPair.EMPTY_GROUP);
		}
		return groupPairResolver.resolve(beanMeta, paramsGroup, groupBy.getSql());
	}

	protected String buildSqlSnippet(String sqlSnippet, List<SqlSnippet.SqlPara> sqlParas, Map<String, Object> paraMap, List<Object> paraReceiver) {
		for (SqlSnippet.SqlPara sqlPara : sqlParas) {
			Object para = paraMap.get(sqlPara.getName());
			if (sqlPara.isJdbcPara()) {
				paraReceiver.add(para);
			} else {
				// 将这部分逻辑提上来，当 sqlSnippet 只有一个拼接参数 且 该参数为空时，使其不参与 where 子句
				String strParam = para != null ? para.toString() : "";
				sqlSnippet = sqlSnippet.replace(sqlPara.getSqlName(), strParam);
			}
		}
		return sqlSnippet;
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
		StringBuilder builder = new StringBuilder(fromWhereSql);
		int count = orderBys.size();
		if (count > 0) {
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
		SqlSnippet orderBySnippet = beanMeta.getOrderBySnippet();
		if (count == 0 && StringUtils.isNotBlank(orderBySnippet.getSql())) {
			SqlWrapper<Object> dbFieldSql = resolveDbFieldSql(orderBySnippet, paraMap);
			String orderBySql = dbFieldSql.getSql();
			if (StringUtils.isNotBlank(orderBySql)) {
				builder.append(" order by ").append(orderBySql);
				SqlWrapper<Object> sqlWrapper = forPaginate(fieldSelectSql, builder.toString(), paging);
				SqlWrapper<Object> listSql = new SqlWrapper<>(sqlWrapper.getSql());
				listSql.addParas(dbFieldSql.getParas());
				listSql.addParas(sqlWrapper.getParas());
				return listSql;
			}
		}
		return forPaginate(fieldSelectSql, builder.toString(), paging);
	}

	protected SqlWrapper<Object> resolveTableSql(SqlSnippet tableSnippet, Map<String, Object> paraMap) {
		SqlWrapper<Object> sqlWrapper = new SqlWrapper<>();
		String tables = tableSnippet.getSql();
		List<SqlSnippet.SqlPara> params = tableSnippet.getParas();
		tables = buildSqlSnippet(tables, params, paraMap, sqlWrapper.getParas());
		sqlWrapper.setSql(tables);
		return sqlWrapper;
	}

	protected SqlWrapper<Object> resolveDbFieldSql(SqlSnippet dbFieldSnippet, Map<String, Object> paraMap) {
		String dbField = dbFieldSnippet.getSql();
		List<SqlSnippet.SqlPara> params = dbFieldSnippet.getParas();
		SqlWrapper<Object> sqlWrapper = new SqlWrapper<>();
		dbField = buildSqlSnippet(dbField, params, paraMap, sqlWrapper.getParas());
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

	public GroupPair.Resolver getGroupPairResolver() {
		return groupPairResolver;
	}

	public void setGroupPairResolver(GroupPair.Resolver groupPairResolver) {
		this.groupPairResolver = Objects.requireNonNull(groupPairResolver);
	}

}
