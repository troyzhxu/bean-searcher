package com.ejlchina.searcher;

import com.ejlchina.searcher.param.FetchInfo;

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
	PageExtractor getPagination();

	/**
	 * @param metadata 元数据
	 * @param fetchInfo Fetch 信息
	 * @param paraMap 原始检索参数
	 * @return SearchParam
	 * */
	<T> SearchParam resolve(Metadata<T> metadata, FetchInfo fetchInfo, Map<String, Object> paraMap);
	
}
