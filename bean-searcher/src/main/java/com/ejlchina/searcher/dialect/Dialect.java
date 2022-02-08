package com.ejlchina.searcher.dialect;

import com.ejlchina.searcher.SqlWrapper;
import com.ejlchina.searcher.param.Paging;

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
	 * @return 分页 Sql
	 */
	SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging);

}
