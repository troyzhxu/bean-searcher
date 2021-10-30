package com.ejlchina.searcher;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 检索 SQL 执行接口
 * 
 * */
public interface SearchSqlExecutor {

	/**
	 * 
	 * @param searchSql 检索 SQL
	 * @return 执行结果
	 */
	<T> SqlResult<T> execute(SearchSql<T> searchSql);
	
}
