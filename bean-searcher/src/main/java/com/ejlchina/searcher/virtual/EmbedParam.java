package com.ejlchina.searcher.virtual;

/**
 * 内嵌参数
 */
public class EmbedParam {

	/**
	 * 内嵌参数名
	 */
	private String name;
	
	/**
	 * 在 Sql 里的参数名（待前后缀）
	 */
	private String sqlName;
	
	/**
	 * 是否可作为 JDBC 的参数
	 */
	private boolean parameterized = false;

	public EmbedParam(String sqlName) {
		this.sqlName = sqlName;
	}

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
