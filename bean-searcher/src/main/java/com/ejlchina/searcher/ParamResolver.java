package com.ejlchina.searcher;

import java.util.List;
import java.util.Map;

import com.ejlchina.searcher.implement.pagination.Pagination;


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
	 * @param fieldList 可以参与检索的属性列表
	 * @param paraMap 用户请求参数映射
	 * @return SearchParam
	 * */
	SearchParam resolve(List<String> fieldList, Map<String, Object> paraMap);
	
	
}
