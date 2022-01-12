package com.ejlchina.searcher.dialect;

import com.ejlchina.searcher.param.Paging;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库方言
 * @author Troy.Zhou
 * @since v1.0
 */
public interface Dialect {

	/**
	 * 把字段 dbField 转换为大写
	 * @param builder sql builder
	 * @param dbField 数据库字段
	 */
	void toUpperCase(StringBuilder builder, String dbField);

	/**
	 * 分页
	 * @param fieldSelectSql 查询语句
	 * @param fromWhereSql 条件语句
	 * @param paging 分页参数
	 * @return 分页Sql
	 */
	PaginateSql forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging);

	
	/**
	 * 分页 SQL
	 * @author Troy.Zhou
	 */
	class PaginateSql {

		private String sql;

		private final List<Object> params = new ArrayList<>(2);

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
