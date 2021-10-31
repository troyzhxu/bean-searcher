package com.ejlchina.searcher;

import java.util.Map;

/**
 * 检索参数过滤器
 * @author Troy.Zhou @ 2021-10-30
 */
public interface ParamFilter {

	/**
	 * @param metadata 元信息
	 * @param paraMap 过滤前的检索参数
	 * @param <T> 泛型
	 * @return 过滤后的检索参数
	 */
	<T> Map<String, Object> doFilter(Metadata<T> metadata, Map<String, Object> paraMap);
	
}
