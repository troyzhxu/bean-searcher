package com.ejlchina.searcher;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 检索SQL解析接口
 * 
 * */
public interface SqlResolver {


	<T> SearchSql<T> resolve(Metadata<T> metadata, SearchParam searchParam);
	
	
}
