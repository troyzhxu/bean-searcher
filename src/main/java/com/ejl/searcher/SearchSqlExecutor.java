package com.ejl.searcher;


/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 检索SQL执行接口
 * 
 * */
public interface SearchSqlExecutor {

	
	public SearchTmpResult execute(SearchSql searchSql);
	
}
