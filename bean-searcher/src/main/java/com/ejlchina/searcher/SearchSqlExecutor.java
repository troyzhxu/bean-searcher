package com.ejlchina.searcher;

import java.util.Map;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 检索SQL执行接口
 * 
 * */
public interface SearchSqlExecutor {

	/**
	 * 
	 * @param searchSql 检索 SQL
	 * @return 执行结果
	 */
	SqlResult execute(SearchSql searchSql);
	
}
