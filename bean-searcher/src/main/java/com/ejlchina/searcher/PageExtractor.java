package com.ejlchina.searcher;

import com.ejlchina.searcher.param.LimitParam;

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
	 */
	LimitParam extract(Map<String, Object> paraMap);
	
	/**
	 * @return 最大条数的参数名
	 * */
	String getSizeName();
	
}
