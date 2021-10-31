package com.ejlchina.searcher;

import com.ejlchina.searcher.implement.pagination.Pagination;
import com.ejlchina.searcher.param.SearchParam;

import java.util.Map;

/***
 * 请求参数解析器接口
 * 
 * @author Troy.Zhou @ 2017-03-20
 * */
public interface ParamResolver {

	/**
	 * @return 分页解析器
	 * */
	Pagination getPagination();

	/**
	 * @param metadata 元数据
	 * @param paraMap 原始检索参数
	 * @return SearchParam
	 * */
	<T> SearchParam resolve(Metadata<T> metadata, Map<String, Object> paraMap);
	
}
