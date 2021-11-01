package com.ejlchina.searcher;

import java.lang.reflect.Method;
import java.util.*;

/**
 * SearchBean 的属性与数据库表字段的映射信息
 *  
 * @author Troy.Zhou @ 2017-03-20
 * 
 */
public class Metadata<T> {

	/**
	 * 用户 Bean Class
	 */
	private final Class<T> beanClass;

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
	 * Bean 的属性列表（已排好序）
	 * */
	private final List<String> fieldList = new ArrayList<>();

	/**
	 * 映射: Bean属性-> DB字段
	 * */
	private final Map<String, SqlSnippet> fieldDbSnippetMap = new HashMap<>();

	/**
	 * 映射: Bean属性 -> DB字段别名
	 * */
	private final Map<String, String> fieldDbAliasMap = new HashMap<>();
	
	/**
	 * 映射: Bean属性 -> 属性GET方法
	 * */
	private final Map<String, Method> fieldGetMethodMap = new HashMap<>();
	
	/**
	 * 映射: Bean属性 -> 属性类型
	 * */
	private final Map<String, Class<?>> fieldTypeMap = new HashMap<>();



	public Metadata(Class<T> beanClass, SqlSnippet tableSnippet, SqlSnippet joinCondSnippet, SqlSnippet groupBySnippet, boolean distinct) {
		this.beanClass = beanClass;
		this.tableSnippet = tableSnippet;
		this.joinCondSnippet = joinCondSnippet;
		this.groupBySnippet = groupBySnippet;
		this.distinct = distinct;
	}

	
	public void addFieldDbMap(String field, SqlSnippet fieldSnippet, Method getMethod, Class<?> fieldType) {
		if (fieldList.contains(field)) {
			throw new SearchException("不可以重复添加字段");
		}
		String dbField = fieldSnippet.getSnippet();
        if (dbField.toLowerCase().startsWith("select ")) {
			fieldSnippet.setSnippet("(" + dbField + ")");
        }
		fieldList.add(field);
		fieldDbAliasMap.put(field, "d_" + fieldList.size());
		fieldGetMethodMap.put(field, getMethod);
		fieldTypeMap.put(field, fieldType);
		fieldDbSnippetMap.put(field, fieldSnippet);
	}

	public Class<T> getBeanClass() {
		return beanClass;
	}

	public SqlSnippet getTableSnippet() {
		return tableSnippet;
	}

	public String getJoinCond() {
		return joinCondSnippet.getSnippet();
	}

	public List<SqlSnippet.Param> getJoinCondEmbedParams() {
		return joinCondSnippet.getParams();
	}

	public String getGroupBy() {
		return groupBySnippet.getSnippet();
	}

	public List<SqlSnippet.Param> getGroupByEmbedParams() {
		return groupBySnippet.getParams();
	}

	public boolean isDistinct() {
		return distinct;
	}

	public String getDbField(String field) {
		SqlSnippet snippet = fieldDbSnippetMap.get(field);
		return snippet != null ? snippet.getSnippet() : null;
	}

	public SqlSnippet getDbFieldSnippet(String field) {
		return fieldDbSnippetMap.get(field);
	}

	public Map<String, String> getFieldDbAliasMap() {
		return fieldDbAliasMap;
	}

	public Set<Map.Entry<String, String>> getFieldDbAliasEntrySet() {
		return fieldDbAliasMap.entrySet();
	}

	public List<String> getFieldList() {
		return fieldList;
	}

	public Map<String, Method> getFieldGetMethodMap() {
		return fieldGetMethodMap;
	}

	public Map<String, Class<?>> getFieldTypeMap() {
		return fieldTypeMap;
	}
	
}
