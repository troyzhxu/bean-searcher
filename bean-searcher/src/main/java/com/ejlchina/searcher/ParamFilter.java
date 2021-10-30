package com.ejlchina.searcher;

import java.util.Map;

/**
 * 参数过滤器
 * @author Troy.Zhou @ 2021-10-30
 */
public interface ParamFilter {

	
	<T> Map<String, Object> doFilter(Metadata<T> metadata, Map<String, Object> paraMap);
	
	
}
