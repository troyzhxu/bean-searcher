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
	 */
	Paging extract(Map<String, Object> paraMap);

	/**
	 * 分页参数纠正
	 * @param paging 原始分页参数
	 * @return 纠正后的分页参数
	 * @since v3.6.1
	 */
	Paging correct(Paging paging);

	/**
	 * @return 最大条数的参数名
	 * */
	String getSizeName();
	
}
