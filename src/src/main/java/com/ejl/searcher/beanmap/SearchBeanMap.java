package com.ejl.searcher.beanmap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户Bean的属性与数据库表字段的映射信息
 *  
 * @author Troy.Zhou @ 2017-03-20
 * 
 */
public class SearchBeanMap {

	/**
	 * 需要查询的数据表
	 * */
	private String talbes;
	
	/**
	 * 连表条件
	 * */
	private String joinCond;
	
	/**
	 * 分组字段
	 * */
	private String groupBy;
	
	/**
	 * 是否 distinct 结果
	 * */
	private boolean distinct;
	
	/**
	 * 参与检索的Bean属性列表
	 * */
	private List<String> fieldList = new ArrayList<>();
	
	/**
	 * 映射: Bean属性-> DB字段
	 * */
	private Map<String, String> fieldDbMap = new HashMap<>();
	
	/**
	 * 映射: Bean属性 -> DB字段别名
	 * */
	private Map<String, String> fieldDbAliasMap = new HashMap<>();
	
	/**
	 * 映射: Bean属性 -> DB字段别名
	 * */
	private Map<String, Method> fieldGetMethodMap = new HashMap<>();
	
	/**
	 * 映射: Bean属性 -> Bean属性类型
	 * */
	private Map<String, Class<?>> fieldTypeMap = new HashMap<>();
	
	
	public SearchBeanMap(String talbes, String joinCond, String groupBy, boolean distinct) {
		this.talbes = talbes;
		this.joinCond = joinCond;
		this.groupBy = groupBy;
		this.distinct = distinct;
	}

	
	public void addFieldDbMap(String field, String dbField, Method getMethod, Class<?> fieldType) {
		if (fieldList.contains(field)) {
			throw new RuntimeException("不可以重复添加字段");
		}
        if (dbField.toLowerCase().startsWith("select ")) {
            dbField = "(" + dbField + ")";
        }
		fieldList.add(field);
		fieldDbMap.put(field, dbField);
		fieldDbAliasMap.put(field, "d_" + fieldList.size());
		fieldGetMethodMap.put(field, getMethod);
		fieldTypeMap.put(field, fieldType);
	}
	
	public String getTalbes() {
		return talbes;
	}

	public void setTalbes(String talbes) {
		this.talbes = talbes;
	}

	public String getJoinCond() {
		return joinCond;
	}

	public void setJoinCond(String joinCond) {
		this.joinCond = joinCond;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public Map<String, String> getFieldDbMap() {
		return fieldDbMap;
	}

	public Map<String, String> getFieldDbAliasMap() {
		return fieldDbAliasMap;
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
