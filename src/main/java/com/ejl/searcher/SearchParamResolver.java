package com.ejl.searcher;

import java.util.List;
import java.util.Map;

import com.ejl.searcher.param.SearchParam;


/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 请求参数解析器接口
 * 
 * */
public interface SearchParamResolver {

	
	/**
	 * @return 最大条数的参数名
	 * */
	public String getMaxParamName();
	
	/**
	 * @param fieldList 可以参与检索的属性列表
	 * @param paraMap 用户请求参数映射
	 * */
	public SearchParam resolve(List<String> fieldList, Map<String, String> paraMap);
	
	
}
