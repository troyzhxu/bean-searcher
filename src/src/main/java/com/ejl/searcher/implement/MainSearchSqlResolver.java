package com.ejl.searcher.implement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ejl.searcher.SearchSql;
import com.ejl.searcher.SearchSqlResolver;
import com.ejl.searcher.SearcherException;
import com.ejl.searcher.beanmap.SearchBeanMap;
import com.ejl.searcher.dialect.Dialect;
import com.ejl.searcher.dialect.Dialect.PaginateSql;
import com.ejl.searcher.param.FilterParam;
import com.ejl.searcher.param.Operator;
import com.ejl.searcher.param.SearchParam;
import com.ejl.searcher.util.StrUtils;

/**
 * 默认查询SQL解析器
 * 
 * @author Troy.Zhou @ 2017-03-20
 * @since V1.1.1
 */
public class MainSearchSqlResolver implements SearchSqlResolver {

	static final Pattern DATE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

	static final Pattern DATE_MINUTE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}");
	
	static final Pattern DATE_SECOND_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}");

	/**
	 * 数据库方言
	 */
	private Dialect dialect;

	private String virtualParamPrefix = ":";
	
	
	@Override
	public SearchSql resolve(SearchBeanMap searchBeanMap, SearchParam searchParam) {
		if (!searchBeanMap.isVirtualResolved()) {
			resolveVirtualParams(searchBeanMap);
		}
		List<String> fieldList = searchBeanMap.getFieldList();
		Map<String, String> fieldDbMap = searchBeanMap.getFieldDbMap();
		Map<String, String> fieldDbAliasMap = searchBeanMap.getFieldDbAliasMap();
		Map<String, Class<?>> fieldTypeMap = searchBeanMap.getFieldTypeMap();
		
		Map<String, String> virtualParamMap = searchParam.getVirtualParamMap();
		
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
			for (String key: searchBeanMap.getFieldVirtualParams(field)) {
				String sqlParam = virtualParamMap.get(key);
				searchSql.addListSqlParam(sqlParam);
				searchSql.addCountSqlParam(sqlParam);
			}
			searchSql.addAlias(dbAlias);
		}
		String fieldSelectSql = builder.toString();

		builder = new StringBuilder(" from ");
		builder.append(searchBeanMap.getTalbes());
		
		for (String key: searchBeanMap.getTableVirtualParams()) {
			String sqlParam = virtualParamMap.get(key);
			searchSql.addListSqlParam(sqlParam);
			searchSql.addCountSqlParam(sqlParam);
		}
		
		String joinCond = searchBeanMap.getJoinCond();
		boolean hasJoinCond = joinCond != null && !"".equals(joinCond.trim());
		List<FilterParam> filterParamList = searchParam.getFilterParamList();

		if (hasJoinCond || filterParamList.size() > 0) {
			builder.append(" where ");
			if (hasJoinCond) {
				builder.append("(").append(joinCond).append(")");
				for (String key: searchBeanMap.getJoinCondVirtualParams()) {
					String sqlParam = virtualParamMap.get(key);
					searchSql.addListSqlParam(sqlParam);
					searchSql.addCountSqlParam(sqlParam);
				}
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
				searchSql.addCountSqlParam(sqlParam);
			}
		}
		String groupBy = searchBeanMap.getGroupBy();
		if (groupBy != null && !"".equals(groupBy.trim())) {
			builder.append(" group by " + groupBy);
			String fromWhereSql = builder.toString();
			String tableAlias = generateTableAlias(fromWhereSql);
			if (searchBeanMap.isDistinct()) {
				String originalSql = fieldSelectSql + fromWhereSql;
				searchSql.setCountSqlString("select count(1) from (" + originalSql + ") " + tableAlias);
			} else {
				searchSql.setCountSqlString("select count(1) from (select count(1)" + fromWhereSql + ") " + tableAlias);
			}
		} else {
			if (searchBeanMap.isDistinct()) {
				String fromWhereSql = builder.toString();
				String originalSql = fieldSelectSql + fromWhereSql;
				String tableAlias = generateTableAlias(fromWhereSql);
				searchSql.setCountSqlString("select count(1) from (" + originalSql + ") " + tableAlias);
			} else {
				searchSql.setCountSqlString("select count(1)" + builder.toString());
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

	
	private void resolveVirtualParams(SearchBeanMap searchBeanMap) {
		
		VirtualSolution solution = resolveVirtualParams(searchBeanMap.getTalbes());
		searchBeanMap.setTalbes(solution.getSqlSnippet());
		searchBeanMap.setTableVirtualParams(solution.getVirtualParams());
		
		solution = resolveVirtualParams(searchBeanMap.getJoinCond());
		searchBeanMap.setJoinCond(solution.getSqlSnippet());
		searchBeanMap.setJoinCondVirtualParams(solution.getVirtualParams());
		
		Map<String, String> fieldDbMap = searchBeanMap.getFieldDbMap();
		for (String field : searchBeanMap.getFieldList()) {
			solution = resolveVirtualParams(fieldDbMap.get(field));
			fieldDbMap.put(field, solution.getSqlSnippet());
			searchBeanMap.putFieldVirtualParam(field, solution.getVirtualParams());
		}
		
		searchBeanMap.setVirtualResolved(true);
	}


	private VirtualSolution resolveVirtualParams(String sqlSnippet) {
		VirtualSolution solution = new VirtualSolution();
		int index1 = sqlSnippet.indexOf(virtualParamPrefix);
		while (index1 > 0) {
			int index2 = sqlSnippet.indexOf(" ", index1);
			if (index2 < 0) 
				index2 = sqlSnippet.indexOf("+", index1);
			if (index2 < 0)
				index2 = sqlSnippet.indexOf("-", index1);
			if (index2 < 0)
				index2 = sqlSnippet.indexOf("*", index1);
			if (index2 < 0)
				index2 = sqlSnippet.indexOf("/", index1);
			if (index2 < 0)
				index2 = sqlSnippet.indexOf("=", index1);
			if (index2 < 0)
				index2 = sqlSnippet.indexOf("!", index1);
			if (index2 < 0)
				index2 = sqlSnippet.indexOf(">", index1);
			if (index2 < 0)
				index2 = sqlSnippet.indexOf("<", index1);
			if (index2 < 0)
				index2 = sqlSnippet.indexOf(",", index1);
			if (index2 < 0)
				index2 = sqlSnippet.indexOf(")", index1);
			String virtualParam = null;
			if (index2 > 0) {
				virtualParam = sqlSnippet.substring(index1, index2);
			} else {
				virtualParam = sqlSnippet.substring(index1);
			}
			if (StrUtils.isBlank(virtualParam) || virtualParam.length() < 2 || virtualParam.contains(" ") 
					|| virtualParam.contains("+") || virtualParam.contains("-")
					|| virtualParam.contains("*") || virtualParam.contains("/")
					|| virtualParam.contains("=") || virtualParam.contains("!")
					|| virtualParam.contains(">") || virtualParam.contains("<")
					|| virtualParam.contains(",") || virtualParam.contains(")")) {
				throw new SearcherException("这里有一个语法错误：" + sqlSnippet);
			}
			sqlSnippet = sqlSnippet.replace(virtualParam, "?");
			solution.addVirtualParam(virtualParam.substring(1));
			index1 = sqlSnippet.indexOf(virtualParamPrefix);
		}
		solution.setSqlSnippet(sqlSnippet);
		return solution;
	}
	
	
	class VirtualSolution {
		
		String sqlSnippet;
		List<String> virtualParams = new ArrayList<>();

		public String getSqlSnippet() {
			return sqlSnippet;
		}

		public void setSqlSnippet(String sqlSnippet) {
			this.sqlSnippet = sqlSnippet;
		}

		public List<String> getVirtualParams() {
			return virtualParams;
		}

		public void addVirtualParam(String virtualParam) {
			this.virtualParams.add(virtualParam);
		}
		
	}
	
	
	
	
	private String generateTableAlias(String originalSql) {
		String tableAlias = "tbl_a_";
		while (originalSql.contains(tableAlias)) {
			tableAlias += "_";
		}
		return tableAlias;
	}

	/**
	 * @return 查询参数值
	 */
	private List<Object> appendFilterConditionSql(StringBuilder builder, Class<?> fieldType, 
			String dbField, FilterParam filterParam) {
		String[] values = filterParam.getValues();
		boolean ignoreCase = filterParam.isIgnoreCase();
		Operator operator = filterParam.getOperator();
		String firstRealValue = filterParam.firstNotNullValue();
		if (ignoreCase) {
			for (int i = 0; i < values.length; i++) {
				String val = values[i];
				if (val != null) {
					values[i] = val.toUpperCase();
				}
			}
			if (firstRealValue != null) {
				firstRealValue = firstRealValue.toUpperCase();
			}
		}
		if (operator != Operator.MultiValue) {
			if (ignoreCase) {
				dialect.toUpperCase(builder, dbField);
			} else if (Date.class.isAssignableFrom(fieldType) && firstRealValue != null) {
				appendDateFieldWithDialect(builder, dbField, firstRealValue);
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
			if (values[0] == null || StrUtils.isBlank(values[0])) {
				val1Null = true;
			}
			if (values[1] == null || StrUtils.isBlank(values[1])) {
				val2Null = true;
			}
			if (!val1Null && !val2Null) {
				builder.append(" between ? and ? ");
				params.add(values[0]);
				params.add(values[1]);
			} else if (val1Null && !val2Null) {
				builder.append(" <= ? ");
				params.add(values[1]);
			} else if (!val1Null && val2Null) {
				builder.append(" >= ? ");
				params.add(values[0]);
			}
			break;
		case MultiValue:
			builder.append("(");
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				if (value != null && "NULL".equals(value.toUpperCase())) {
					builder.append(dbField).append(" is null");
				} else if (ignoreCase) {
					dialect.toUpperCase(builder, dbField);
					builder.append(" = ?");
					params.add(value);
				} else if (Date.class.isAssignableFrom(fieldType)) {
					appendDateFieldWithDialect(builder, dbField, value);
					builder.append(" = ?");
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

	private void appendDateFieldWithDialect(StringBuilder builder, String dbField, String value) {
		if (DATE_PATTERN.matcher(value).matches()) {
			dialect.truncateToDateStr(builder, dbField);
		} else if (DATE_MINUTE_PATTERN.matcher(value).matches()) {
			dialect.truncateToDateMinuteStr(builder, dbField);
		} else if (DATE_SECOND_PATTERN.matcher(value).matches()) {
			dialect.truncateToDateSecondStr(builder, dbField);
		} else {
			builder.append(dbField);
		}
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	public void setVirtualParamPrefix(String virtualParamPrefix) {
		this.virtualParamPrefix = virtualParamPrefix;
	}
	
}
