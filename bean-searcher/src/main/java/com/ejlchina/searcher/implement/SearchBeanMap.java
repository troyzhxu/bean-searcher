package com.ejlchina.searcher.implement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ejlchina.searcher.SearchResultConvertInfo;
import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.virtual.VirtualParam;

/**
 * SearchBean 的属性与数据库表字段的映射信息
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
	private final String groupBy;
	
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
	private final Map<String, String> fieldDbMap = new HashMap<>();
	
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
	
	/**
	 * table 虚拟参数
	 */
	private List<VirtualParam> tableVirtualParams;
	
	/**
	 * 连接条件 虚拟参数
	 */
	private List<VirtualParam> joinCondVirtualParams;
	
	/**
	 * 映射：属性 -> 虚拟参数 
	 */
	private final Map<String, List<VirtualParam>> fieldVirtualParamsMap = new HashMap<>();
	
	
	private SearchResultConvertInfo<?> convertInfo;
	
	
	public SearchBeanMap(String talbes, String joinCond, String groupBy, boolean distinct) {
		this.talbes = talbes;
		this.joinCond = joinCond;
		this.groupBy = groupBy;
		this.distinct = distinct;
	}

	
	public void addFieldDbMap(String field, String dbField, Method getMethod, Class<?> fieldType) {
		if (fieldList.contains(field)) {
			throw new SearcherException("不可以重复添加字段");
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

	public boolean isDistinct() {
		return distinct;
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
	
	public List<VirtualParam> getTableVirtualParams() {
		return tableVirtualParams;
	}

	public void setTableVirtualParams(List<VirtualParam> tableVirtualParams) {
		this.tableVirtualParams = tableVirtualParams;
	}

	public List<VirtualParam> getJoinCondVirtualParams() {
		return joinCondVirtualParams;
	}

	public void setJoinCondVirtualParams(List<VirtualParam> joinCondVirtualParams) {
		this.joinCondVirtualParams = joinCondVirtualParams;
	}

	public List<VirtualParam> getFieldVirtualParams(String field) {
		return fieldVirtualParamsMap.get(field);
	}

	public void putFieldVirtualParam(String field, List<VirtualParam> virtualParams) {
		fieldVirtualParamsMap.put(field, virtualParams);
	}

	@SuppressWarnings("unchecked")
	public <T> SearchResultConvertInfo<T> getConvertInfo() {
		return (SearchResultConvertInfo<T>) convertInfo;
	}


	public void setConvertInfo(SearchResultConvertInfo<?> convertInfo) {
		this.convertInfo = convertInfo;
	}
	
}
