package com.ejlchina.searcher;

import java.util.ArrayList;
import java.util.List;


/**
 * SQL 片段
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.0.0
 */
public class SqlSnippet {

	/**
	 * SQL 片段
	 */
	private String snippet;

	/**
	 * 内嵌参数
	 */
	private final List<Param> params = new ArrayList<>();
	
	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public List<Param> getParams() {
		return params;
	}

	public void addParam(Param param) {
		this.params.add(param);
	}

	/**
	 * 内嵌参数
	 * @author Troy.Zhou @ 2021-10-30
	 * @since v3.0.0
	 */
	public static class Param {

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
		private boolean jdbcPara = false;

		public Param(String sqlName) {
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

		public boolean isJdbcPara() {
			return jdbcPara;
		}

		public void setJdbcPara(boolean jdbcPara) {
			this.jdbcPara = jdbcPara;
		}

	}

}
