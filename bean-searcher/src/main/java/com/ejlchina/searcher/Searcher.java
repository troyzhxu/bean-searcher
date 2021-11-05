package com.ejlchina.searcher;

import java.util.List;
import java.util.Map;

/**
 * 检索器接口
 * 根据 SearchBean 的 Class 和请求参数，自动检索数据
 * 它有两个子接口：{@link MapSearcher } 与 {@link BeanSearcher }
 *
 * @author Troy.Zhou @ 2017-03-20
 * @since v1.0.0
 * */
public interface Searcher {

	/**
	 * 适合需要分页的查询
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数
	 * @return { 总条数, 数据列表, 统计信息 }
	 * */
	<T> SearchResult<?> search(Class<T> beanClass, Map<String, Object> paraMap);

	/**
	 * 适合需要分页的查询
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数
	 * @param summaryFields 统计字段
	 * @return { 总条数, 数据列表, 统计信息 }
	 * */
	<T> SearchResult<?> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields);

	/**
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return 满足条件的第一条数据
	 * */
	<T> Object searchFirst(Class<T> beanClass, Map<String, Object> paraMap);

	/**
	 * 适合不需要分页的查询
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return 数据列表
	 * */
	<T> List<?> searchList(Class<T> beanClass, Map<String, Object> paraMap);

	/**
	 * 检索满足条件的所有Bean，不支持偏移
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return 数据列表
	 * */
	<T> List<?> searchAll(Class<T> beanClass, Map<String, Object> paraMap);

	/**
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return 数据个数
	 * */
	<T> Number searchCount(Class<T> beanClass, Map<String, Object> paraMap);

	/**
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param field 参与求和的字段
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return 字段求和统计
	 * */
	<T> Number searchSum(Class<T> beanClass, Map<String, Object> paraMap, String field);

	/**
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param fields 参与求和的字段数组
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return 字段求和统计
	 * */
	<T> Number[] searchSum(Class<T> beanClass, Map<String, Object> paraMap, String[] fields);
	
}
