package com.ejlchina.searcher;

import java.util.Map;

/**
 * 检索参数过滤器
 * @author Troy.Zhou @ 2021-10-30
 */
public interface ParamFilter {

	/**
	 * @param beanMeta 元信息
	 * @param paraMap 过滤前的检索参数
	 * @param <T> 泛型
	 * @return 过滤后的检索参数
	 * @throws IllegalArgumentException 抛出非法参数异常后将不进行 SQL 查询
	 */
	<T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) throws IllegalArgumentException;
	
}
