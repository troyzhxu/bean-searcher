package com.ejlchina.searcher;

/**
 * SQL 片段
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.0.0
 */
public class SqlSnippet extends SqlWrapper<SqlSnippet.SqlPara> {

	public SqlSnippet() {
	}

	public SqlSnippet(String sql) {
		super(sql);
	}

	/**
	 * 内嵌参数
	 * @author Troy.Zhou @ 2021-10-30
	 * @since v3.0.0
	 */
	public static class SqlPara {

		/**
		 * 内嵌参数名
		 */
		private String name;

		/**
		 * 在 Sql 里的参数名（带前后缀）
		 */
		private String sqlName;

		/**
		 * 是否可作为 JDBC 的参数
		 */
		private boolean jdbcPara = false;

		public SqlPara(String sqlName) {
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
