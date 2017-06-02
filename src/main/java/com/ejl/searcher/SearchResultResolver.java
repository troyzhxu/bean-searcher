package com.ejl.searcher;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 查询结果解析接口
 * 
 * */
public interface SearchResultResolver {
	
	
	/**
	 * @param dbAliasFieldMap 映射: DB字段别名 -> Bean属性
	 * */
	public <T> SearchResult<T> resolve(SearchResultConvertInfo<T> convertInfo, SearchTmpResult searchTmpResult);

}
