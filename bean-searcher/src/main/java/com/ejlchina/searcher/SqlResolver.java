package com.ejlchina.searcher;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * SQL 解析器
 * */
public interface SqlResolver {

	/**
	 * @param metadata 元信息
	 * @param searchParam 检索参数
	 * @param <T> 泛型
	 * @return 检索 SQL
	 */
	<T> SearchSql<T> resolve(Metadata<T> metadata, SearchParam searchParam);
	
}
