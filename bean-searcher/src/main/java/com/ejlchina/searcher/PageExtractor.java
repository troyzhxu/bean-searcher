package com.ejlchina.searcher;

import com.ejlchina.searcher.param.Paging;

import java.util.Map;

/**
 * 分页提取器
 *  
 * @author Troy.Zhou
 *
 */
public interface PageExtractor {

	/**
	 * @param paraMap 检索参数
	 * @return 分页信息
	 * @throws IllegalParamException 抛出非法参数异常后将终止 SQL 查询
	 */
	Paging extract(Map<String, Object> paraMap) throws IllegalParamException;

	/**
	 * @return 最大条数的参数名
	 * */
	String getSizeName();
	
}
