package com.ejl.searcher;

/**
 * 查询结果解析接口
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 * */
public interface SearchResultResolver {
	
	
	/**
	 * @param <T> bean 类型
	 * @param convertInfo 转换信息
	 * @param searchTmpResult 检索中间结果
	 * @return 检索结果
	 */
	public <T> SearchResult<T> resolve(SearchResultConvertInfo<T> convertInfo, SearchTmpResult searchTmpResult);

}
