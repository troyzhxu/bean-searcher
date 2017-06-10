package com.zhxu.searcher;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class SearchResultConvertInfo<T> {

	private Class<T> beanClass;
	private Set<Entry<String, String>> fieldDbAliasEntrySet;
	private Map<String, Method> fieldGetMethodMap;
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
