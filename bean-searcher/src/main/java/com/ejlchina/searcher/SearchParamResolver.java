package com.ejlchina.searcher;

import java.util.List;
import java.util.Map;

import com.ejlchina.searcher.implement.pagination.Pagination;
import com.ejlchina.searcher.param.SearchParam;


/***
 * 请求参数解析器接口
 * 
 * @author Troy.Zhou @ 2017-03-20
 * */
public interface SearchParamResolver {

	
	/**
	 * @return 最大条数的参数名
	 * */
	public Pagination getPagination();
	

	
	/**
	 * @param fieldList 可以参与检索的属性列表
	 * @param paraMap 用户请求参数映射
	 * @return SearchParam
	 * */
	public SearchParam resolve(List<String> fieldList, Map<String, String> paraMap);
	
	
}
