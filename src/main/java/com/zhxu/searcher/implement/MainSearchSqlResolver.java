package com.zhxu.searcher.implement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.zhxu.searcher.SearchSql;
import com.zhxu.searcher.SearchSqlResolver;
import com.zhxu.searcher.beanmap.SearchBeanMap;
import com.zhxu.searcher.dialect.Dialect;
import com.zhxu.searcher.param.FilterOperator;
import com.zhxu.searcher.param.FilterParam;
import com.zhxu.searcher.param.SearchParam;
import com.zhxu.searcher.utils.StrUtils;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 *         默认查询SQL解析器
 * 
 */
public class MainSearchSqlResolver implements SearchSqlResolver {

	
	static final Pattern DATE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

	/**
	 * 数据库方言
	 * */
	private Dialect dialect;

	
	@Override
	public SearchSql resolve(SearchBeanMap searchBeanMap, SearchParam searchParam) {
		List<String> fieldList = searchBeanMap.getFieldList();
		Map<String, String> fieldDbMap = searchBeanMap.getFieldDbMap();
		Map<String, String> fieldDbAliasMap = searchBeanMap.getFieldDbAliasMap();
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
			builder.append(dbField).append(" ").append(dbAlias);
			if (i < fieldCount - 1) {
				builder.append(", ");
			}
			searchSql.addAlias(dbAlias);
		}
		String selectDbFieldSetSql = builder.toString();

		builder = new StringBuilder(" from ");
		builder.append(searchBeanMap.getTalbes());
		String joinCond = searchBeanMap.getJoinCond();
		boolean hasJoinCond = joinCond != null && !"".equals(joinCond.trim());
		List<FilterParam> filterParamList = searchParam.getFilterParamList();
	
		if (hasJoinCond || filterParamList.size() > 0) {
			builder.append(" where ");
			if (hasJoinCond) {
				builder.append("(").append(joinCond).append(")");
			}
		}
		for (int i = 0; i < filterParamList.size(); i++) {
			if (i > 0 || hasJoinCond) {
				builder.append(" and ");
			}
			FilterParam filterParam = filterParamList.get(i);
			String dbField = fieldDbMap.get(filterParam.getName());
			Object value = filterParam.getValue();
			Object value2 = filterParam.getValue2();
			boolean ignoreCase = filterParam.isIgnoreCase();
			FilterOperator operator = filterParam.getOperator();
			List<Object> sqlParams = appendFilterConditionSql(builder, dbField, value, value2, ignoreCase, operator);
			for (Object sqlParam : sqlParams) {
				searchSql.addListSqlParam(sqlParam);
				searchSql.addCountSqlParam(sqlParam);
			}
		}
		
		String groupBy = searchBeanMap.getGroupBy();
		if (groupBy != null && !"".equals(groupBy.trim())) {
			builder.append(" group by " + groupBy);
		}
		
		String fromWhereSql = builder.toString();
		
		searchSql.setCountSqlString("select count(1) " + fromWhereSql);
		builder = new StringBuilder(selectDbFieldSetSql).append(fromWhereSql);

		String sortDbAlias = fieldDbAliasMap.get(searchParam.getSort());
		if (sortDbAlias != null) {
			builder.append(" order by ").append(sortDbAlias);
			String order = searchParam.getOrder();
			if (order != null) {
				builder.append(" ").append(order);
			}
		}
		Integer max = searchParam.getMax();
		Long offset = searchParam.getOffset();
		if (max != null) {
			if (offset == null) {
				builder.append(" limit ?");
			} else {
				builder.append(" limit ?, ?");
				searchSql.addListSqlParam(offset);
			}
			searchSql.addListSqlParam(max);
		}
		searchSql.setListSqlString(builder.toString());
		return searchSql;
	}

	/**
	 * @return 查询参数值
	 */
	private List<Object> appendFilterConditionSql(StringBuilder builder, String dbField, 
			Object value, Object value2, boolean ignoreCase, FilterOperator operator) {
		if (ignoreCase) {
			dialect.toUpperCase(builder, dbField);
			value = value.toString().toUpperCase();
		} else {
			if (value != null && value instanceof String || (value2 != null && value2 instanceof String)) {
				String strval = (String) value;
				String strval2 = (String) value2;
				if (strval != null && DATE_PATTERN.matcher(strval).matches() 
						|| strval2 != null && DATE_PATTERN.matcher(strval2).matches()) {
					dialect.truncateToDateStr(builder, dbField);
				} else {
					builder.append(dbField);
				}
			} else {
				builder.append(dbField);
			}
		}
		List<Object> params = new ArrayList<>(2);
		switch (operator) {
		case Include:
			builder.append(" like '%").append(value).append("%'");
			break;
		case Equal:
			builder.append(" = ?");
			params.add(value);
			break;
		case GreaterThan:
			builder.append(" > ?");
			params.add(value);
			break;
		case LessThan:
			builder.append(" < ?");
			params.add(value);
			break;
		case NotEqual:
			builder.append(" != ?");
			params.add(value);
			break;
		case Empty:
			builder.append(" is null");
			break;
		case NotEmpty:
			builder.append(" is not null");
			break;
		case StartWith:
			builder.append(" like '").append(value).append("%'");
			break;
		case EndWith:
			builder.append(" like '%").append(value).append("'");
			break;
		case Between:
			boolean val1Null = false;
			boolean val2Null = false;
			if (value == null || StrUtils.isBlank(value.toString())) {
				val1Null = true;
			}
			if (value2 == null || StrUtils.isBlank(value2.toString())) {
				val2Null = true;
			}
			if (!val1Null && !val2Null) {
				builder.append(" between ? and ? ");
				params.add(value);
				params.add(value2);
			} else if (val1Null && !val2Null) {
				builder.append(" <= ? ");
				params.add(value2);
			} else if (!val1Null && val2Null) {
				builder.append(" >= ? ");
				params.add(value);
			}
		}
		return params;
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}
	
	
}
