package com.ejlchina.searcher;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


/**
 * 检索结果转换信息
 * @author Troy.Zhou
 *
 * @param <T> 用户 Bean 类型
 */
public class SearchResultConvertInfo<T> {

	/**
	 * 用户 Bean Class
	 */
	private Class<T> beanClass;
	
	/**
	 * 用户 Bean 属性 到 数据库字段别名 的 Entry 集合
	 */
	private Set<Entry<String, String>> fieldDbAliasEntrySet;
	
	/**
	 * 用户 Bean 属性 到 属性  get 方法 的 映射
	 */
	private Map<String, Method> fieldGetMethodMap;
	
	/**
	 * 用户 Bean 属性 到 属性类型 的 映射
	 */
	private Map<String, Class<?>> fieldTypeMap;

	
	public SearchResultConvertInfo(Class<T> beanClass) {
		this.beanClass = beanClass;
	}

	public Class<T> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<T> beanClass) {
		this.beanClass = beanClass;
	}

	public Set<Entry<String, String>> getFieldDbAliasEntrySet() {
		return fieldDbAliasEntrySet;
	}

	public void setFieldDbAliasEntrySet(Set<Entry<String, String>> fieldDbAliasEntrySet) {
		this.fieldDbAliasEntrySet = fieldDbAliasEntrySet;
	}

	public Map<String, Method> getFieldGetMethodMap() {
		return fieldGetMethodMap;
	}

	public void setFieldGetMethodMap(Map<String, Method> fieldGetMethodMap) {
		this.fieldGetMethodMap = fieldGetMethodMap;
	}

	public Map<String, Class<?>> getFieldTypeMap() {
		return fieldTypeMap;
	}

	public void setFieldTypeMap(Map<String, Class<?>> fieldTypeMap) {
		this.fieldTypeMap = fieldTypeMap;
	}

}
