package com.ejl.searcher.dialect;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库方言
 * 
 * @author Troy.Zhou
 * 
 */
public interface Dialect {

	/**
	 * 把字段 dbField 转换为大写
	 * 
	 * @param builder
	 *            sql builder
	 * @param dbField
	 *            数据库字段
	 */
	void toUpperCase(StringBuilder builder, String dbField);

	/**
	 * 把字段 dbField 截取为 YYYY-MM-DD 格式字符串
	 * 
	 * @param builder
	 *            sql builder
	 * @param dbField
	 *            数据库字段
	 */
	void truncateToDateStr(StringBuilder builder, String dbField);

	/**
	 * 分页
	 * 
	 * @param fieldSelectSql
	 *            查询语句
	 * @param fromWhereSql
	 *            条件语句
	 * @param max
	 *            最大条数
	 * @param offset
	 *            偏移条数
	 * @return 分页Sql
	 */
	PaginateSql forPaginate(String fieldSelectSql, String fromWhereSql, Integer max, Long offset);

	
	/**
	 * 分页Sq
	 * @author Troy.Zhou
	 * @since
	 */
	public class PaginateSql {

		private String sql;
		private List<Object> params = new ArrayList<>(2);

		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

		public List<Object> getParams() {
			return params;
		}

		public void addParam(Object param) {
			params.add(param);
		}

	}

}
