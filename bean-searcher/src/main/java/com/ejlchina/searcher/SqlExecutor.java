package com.ejlchina.searcher;

/**
 * SQL 执行器
 * @author Troy.Zhou @ 2017-03-20
 * @since v1.0.0
 * */
public interface SqlExecutor {

	/**
	 * @param <T> 泛型
	 * @param searchSql 检索 SQL
	 * @return 执行结果
	 */
	<T> SqlResult<T> execute(SearchSql<T> searchSql);
	
}
