package com.ejlchina.searcher;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 检索SQL执行接口
 * 
 * */
public interface SearchSqlExecutor {

	/**
	 * 
	 * @param searchSql 检索SQL
	 * @return 检索临时结果
	 */
	SearchMapResult execute(SearchSql searchSql);
	
}
