package com.ejlchina.searcher;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * SQL 执行器
 * */
public interface SqlExecutor {

	/**
	 * @param searchSql 检索 SQL
	 * @return 执行结果
	 */
	<T> SqlResult<T> execute(SearchSql<T> searchSql);
	
}
