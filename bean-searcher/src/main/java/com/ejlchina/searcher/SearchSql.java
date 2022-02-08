package com.ejlchina.searcher;

import java.util.ArrayList;
import java.util.List;

/**
 * 检索的 SQL 信息
 *  
 * @author Troy.Zhou @ 2017-03-20
 * 
 * */
public class SearchSql<T> {

	/**
	 * 待检索数据的元信息
	 */
	private final BeanMeta<T> beanMeta;

	/**
	 * 需要 Select 的字段
	 */
	private final List<String> fetchFields;

	/**
	 * 查询数据列表的SQL
	 * */
	private String listSqlString;
	
	/**
	 * 查询聚族信息的SQL
	 * */
	private String clusterSqlString;
	
	/**
	 * 查询数据列表的参数
	 * */
	private final List<Object> listSqlParams = new ArrayList<>();
	
	/**
	 * 聚族查询的参数
	 * */
	private final List<Object> clusterSqlParams = new ArrayList<>();

	/**
	 * 总条数别名
	 */
	private String countAlias;
	
	/**
	 * 求和字段别名
	 */
	private final List<String> summaryAliases = new ArrayList<>();

	/**
	 * 是否应该查询总条数
	 * */
	private boolean shouldQueryCluster;
	
	/**
	 * 是否应该查询数据列表
	 * */
	private boolean shouldQueryList;


	public SearchSql(BeanMeta<T> beanMeta, List<String> fetchFields) {
		this.beanMeta = beanMeta;
		this.fetchFields = fetchFields;
	}

	public BeanMeta<T> getBeanMeta() {
		return beanMeta;
	}

	public List<String> getFetchFields() {
		return fetchFields;
	}

	public String getListSqlString() {
		return listSqlString;
	}

	public void setListSqlString(String sqlString) {
		this.listSqlString = sqlString;
	}

	public List<Object> getListSqlParams() {
		return listSqlParams;
	}

	public void addListSqlParam(Object sqlParam) {
		this.listSqlParams.add(sqlParam);
	}

	public void addListSqlParams(List<Object> sqlParams) {
		this.listSqlParams.addAll(sqlParams);
	}

	public String getClusterSqlString() {
		return clusterSqlString;
	}

	public void setClusterSqlString(String sqlString) {
		this.clusterSqlString = sqlString;
	}

	public List<Object> getClusterSqlParams() {
		return clusterSqlParams;
	}

	public void addClusterSqlParam(Object sqlParam) {
		this.clusterSqlParams.add(sqlParam);
	}

	public void addClusterSqlParams(List<Object> sqlParams) {
		this.clusterSqlParams.addAll(sqlParams);
	}

	public String getCountAlias() {
		return countAlias;
	}

	public void setCountAlias(String countAlias) {
		this.countAlias = countAlias;
	}

	public void addSummaryAlias(String alias) {
		summaryAliases.add(alias);
	}
	
	public List<String> getSummaryAliases() {
		return summaryAliases;
	}

	public boolean isShouldQueryCluster() {
		return shouldQueryCluster;
	}

	public void setShouldQueryCluster(boolean shouldQueryCluster) {
		this.shouldQueryCluster = shouldQueryCluster;
	}

	public boolean isShouldQueryList() {
		return shouldQueryList;
	}

	public void setShouldQueryList(boolean shouldQueryList) {
		this.shouldQueryList = shouldQueryList;
	}
	
}
