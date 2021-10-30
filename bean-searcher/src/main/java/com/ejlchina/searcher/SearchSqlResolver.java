package com.ejlchina.searcher;

import com.ejlchina.searcher.param.SearchParam;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 检索SQL解析接口
 * 
 * */
public interface SearchSqlResolver {


	SearchSql resolve(Metadata metadata, SearchParam searchParam);
	
	
}
