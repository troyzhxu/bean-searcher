package com.ejl.searcher;

import java.util.ArrayList;
import java.util.List;


/**
 * 检索的 SQL 信息
 *  
 * @author Troy.Zhou @ 2017-03-20
 * 
 * */
public class SearchSql {

	/**
	 * 查询数据列表的SQL
	 * */
	private String listSqlString;
	
	/**
	 * 查询数据总数的SQL
	 * */
	private String countSqlString;
	
	/**
	 * 查询数据列表的参数
	 * */
	private List<Object> listSqlParams = new ArrayList<>();
	
	/**
	 * 查询数据总数的参数
	 * */
	private List<Object> countSqlParams = new ArrayList<>();
	
	/**
	 * 别名列表
	 * */
	private List<String> aliasList = new ArrayList<>();
	
	/**
	 * 是否应该查询总条数
	 * */
	private boolean shouldQueryTotal;
	
	/**
	 * 是否应该查询数据列表
	 * */
	private boolean shouldQueryList;
	
	
	public SearchSql() {
	}

	
	public String getListSqlString() {
		return listSqlString;
	}

	public void setListSqlString(String listSqlString) {
		this.listSqlString = listSqlString;
	}

	public List<Object> getListSqlParams() {
		return listSqlParams;
	}

	public void addListSqlParam(Object listSqlParam) {
		this.listSqlParams.add(listSqlParam);
	}

	public String getCountSqlString() {
		return countSqlString;
	}

	public void setCountSqlString(String countSqlString) {
		this.countSqlString = countSqlString;
	}

	public List<Object> getCountSqlParams() {
		return countSqlParams;
	}

	public void addCountSqlParam(Object countSqlParam) {
		this.countSqlParams.add(countSqlParam);
	}

	public void addAlias(String alias) {
		aliasList.add(alias);
	}
	
	public List<String> getAliasList() {
		return aliasList;
	}

	public boolean isShouldQueryTotal() {
		return shouldQueryTotal;
	}

	public void setShouldQueryTotal(boolean shouldQueryTotal) {
		this.shouldQueryTotal = shouldQueryTotal;
	}


	public boolean isShouldQueryList() {
		return shouldQueryList;
	}


	public void setShouldQueryList(boolean shouldQueryList) {
		this.shouldQueryList = shouldQueryList;
	}
	
	
	
}
