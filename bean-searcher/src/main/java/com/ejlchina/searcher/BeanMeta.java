package com.ejlchina.searcher;

import com.ejlchina.searcher.util.StringUtils;

import java.util.*;

/**
 * SearchBean 的元信息
 *
 * @author Troy.Zhou @ 2017-03-20
 * @since v3.0.0
 */
public class BeanMeta<T> {

	/**
	 * 用户 Bean Class
	 */
	private final Class<T> beanClass;

	/**
	 * 所属数据源
	 */
	private final String dataSource;

	/**
	 * 需要查询的数据表
	 * */
	private final SqlSnippet tableSnippet;

	/**
	 * 连表条件
	 * */
	private final SqlSnippet joinCondSnippet;
	
	/**
	 * 分组字段
	 * */
	private final SqlSnippet groupBySnippet;
	
	/**
	 * 是否 distinct 结果
	 * */
	private final boolean distinct;

	/**
	 * 映射: Bean属性 -> 属性元信息
	 * */
	private final Map<String, FieldMeta> fieldMetaMap = new HashMap<>();


	public BeanMeta(Class<T> beanClass, String dataSource, SqlSnippet tableSnippet, SqlSnippet joinCondSnippet,
					SqlSnippet groupBySnippet, boolean distinct) {
		this.beanClass = beanClass;
		this.dataSource = dataSource;
		this.tableSnippet = tableSnippet;
		this.joinCondSnippet = joinCondSnippet;
		this.groupBySnippet = groupBySnippet;
		this.distinct = distinct;
	}

	public void addFieldMeta(String field, FieldMeta meta) {
		if (fieldMetaMap.containsKey(field)) {
			throw new SearchException("不可以重复添加字段：" + field);
		}
		fieldMetaMap.put(field, meta);
	}

	public Class<T> getBeanClass() {
		return beanClass;
	}

	public String getDataSource() {
		return dataSource;
	}

	public SqlSnippet getTableSnippet() {
		return tableSnippet;
	}

	public String getJoinCond() {
		return joinCondSnippet.getSql();
	}

	public List<SqlSnippet.SqlPara> getJoinCondSqlParas() {
		return joinCondSnippet.getParas();
	}

	public String getGroupBy() {
		return groupBySnippet.getSql();
	}

	public List<SqlSnippet.SqlPara> getGroupBySqlParas() {
		return groupBySnippet.getParas();
	}

	public boolean isDistinct() {
		return distinct;
	}

	public Set<String> getFieldSet() {
		return Collections.unmodifiableSet(fieldMetaMap.keySet());
	}

	public int getFieldCount() {
		return fieldMetaMap.size();
	}

	public FieldMeta requireFieldMeta(String field) {
		FieldMeta meta = getFieldMeta(field);
		if (meta == null) {
			throw new IllegalStateException("No such field named: " + field);
		}
		return meta;
	}

	public FieldMeta getFieldMeta(String field) {
		if (field != null) {
			return fieldMetaMap.get(field);
		}
		return null;
	}

	/**
	 * 获取某字段的 SQL 片段
	 * @param field Java 字段名
	 * @return SQL 片段
	 */
	public String getFieldSql(String field) {
		FieldMeta meta = requireFieldMeta(field);
		return meta.getFieldSql().getSql();
	}

	public Collection<FieldMeta> getFieldMetas() {
		return Collections.unmodifiableCollection(fieldMetaMap.values());
	}

	public boolean isDistinctOrGroupBy() {
		return distinct || StringUtils.isNotBlank(groupBySnippet.getSql());
	}

}
