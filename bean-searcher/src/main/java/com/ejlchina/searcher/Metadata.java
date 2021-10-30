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
	private final EmbedSolution tableSolution;

	/**
	 * 连表条件
	 * */
	private final EmbedSolution joinCondSolution;
	
	/**
	 * 分组字段
	 * */
	private final EmbedSolution groupBySolution;
	
	/**
	 * 是否 distinct 结果
	 * */
	private final boolean distinct;
	
	/**
	 * 参与检索的Bean属性列表
	 * */
	private final List<String> fieldList = new ArrayList<>();

	/**
	 * 映射: Bean属性-> DB字段
	 * */
	private final Map<String, EmbedSolution> fieldDbSolutionMap = new HashMap<>();

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
	
	
	private SearchResultConvertInfo<?> convertInfo;


	public Metadata(Class<T> beanClass, EmbedSolution tableSolution, EmbedSolution joinCondSolution, EmbedSolution groupBySolution, boolean distinct) {
		this.beanClass = beanClass;
		this.tableSolution = tableSolution;
		this.joinCondSolution = joinCondSolution;
		this.groupBySolution = groupBySolution;
		this.distinct = distinct;
	}

	
	public void addFieldDbMap(String field, EmbedSolution dbFieldSolution, Method getMethod, Class<?> fieldType) {
		if (fieldList.contains(field)) {
			throw new SearcherException("不可以重复添加字段");
		}
		String dbField = dbFieldSolution.getSqlSnippet();
        if (dbField.toLowerCase().startsWith("select ")) {
			dbFieldSolution.setSqlSnippet("(" + dbField + ")");
        }
		fieldList.add(field);
		fieldDbAliasMap.put(field, "d_" + fieldList.size());
		fieldGetMethodMap.put(field, getMethod);
		fieldTypeMap.put(field, fieldType);
		fieldDbSolutionMap.put(field, dbFieldSolution);
	}

	public Class<T> getBeanClass() {
		return beanClass;
	}

	public String getTalbes() {
		return tableSolution.getSqlSnippet();
	}

	public List<EmbedParam> getTableEmbedParams() {
		return tableSolution.getParams();
	}

	public String getJoinCond() {
		return joinCondSolution.getSqlSnippet();
	}

	public List<EmbedParam> getJoinCondEmbedParams() {
		return joinCondSolution.getParams();
	}

	public String getGroupBy() {
		return groupBySolution.getSqlSnippet();
	}

	public List<EmbedParam> getGroupByEmbedParams() {
		return groupBySolution.getParams();
	}

	public boolean isDistinct() {
		return distinct;
	}

	public String getDbField(String field) {
		EmbedSolution solution = fieldDbSolutionMap.get(field);
		return solution != null ? solution.getSqlSnippet() : null;
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

	public List<EmbedParam> getFieldEmbedParams(String field) {
		EmbedSolution solution = fieldDbSolutionMap.get(field);
		return solution != null ? solution.getParams() : Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	public <T> SearchResultConvertInfo<T> getConvertInfo() {
		return (SearchResultConvertInfo<T>) convertInfo;
	}


	public void setConvertInfo(SearchResultConvertInfo<?> convertInfo) {
		this.convertInfo = convertInfo;
	}
	
}
