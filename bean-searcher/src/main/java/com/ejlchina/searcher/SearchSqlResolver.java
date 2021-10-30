package com.ejlchina.searcher;

import com.ejlchina.searcher.param.SearchParam;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 检索SQL解析接口
 * 
 * */
public interface SearchSqlResolver {


	<T> SearchSql<T> resolve(Metadata<T> metadata, SearchParam searchParam);
	
	
}
