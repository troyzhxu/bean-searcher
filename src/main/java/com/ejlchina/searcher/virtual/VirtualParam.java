package com.ejlchina.searcher.virtual;

public class VirtualParam {

	/**
	 * 虚拟参数名
	 */
	private String name;
	
	/**
	 * 在Sql里的参数名
	 */
	private String sqlName;
	
	/**
	 * 是否可作为JDBC的参数
	 */
	private boolean parameterized = false;

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getSqlName() {
		return sqlName;
	}

	public void setSqlName(String sqlName) {
		this.sqlName = sqlName;
	}

	public boolean isParameterized() {
		return parameterized;
	}

	public void setParameterized(boolean parameterized) {
		this.parameterized = parameterized;
	}
	
}
