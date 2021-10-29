package com.ejlchina.searcher;

import java.util.List;
import java.util.Map;


/**
 * 自动检索器接口
 * 根据 Bean 的 Class 和请求参数，自动检索 Bean
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 * */
public interface Searcher {

	/**
	 * 适合需要分页的查询
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数
	 * @return 总条数，Bean 数据列表
	 * */
	<T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap);

	/**
	 * 适合需要分页的查询
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数
	 * @param summaryFields 统计字段
	 * @return 总条数，Bean 数据列表
	 * */
	<T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields);

	/**
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return 满足条件的第一个Bean 
	 * */
	<T> T searchFirst(Class<T> beanClass, Map<String, Object> paraMap);

	/**
	 * 适合不需要分页的查询
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return Bean 数据列表
	 * */
	<T> List<T> searchList(Class<T> beanClass, Map<String, Object> paraMap);

	/**
	 * 检索满足条件的所有Bean，不支持偏移
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return Bean 数据列表
	 * */
	<T> List<T> searchAll(Class<T> beanClass, Map<String, Object> paraMap);

	/**
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return Bean 数据个数
	 * */
	<T> Number searchCount(Class<T> beanClass, Map<String, Object> paraMap);

	/**
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param field 参与求和的字段
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return Bean 字段求和
	 * */
	<T> Number searchSum(Class<T> beanClass, Map<String, Object> paraMap, String field);

	/**
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param fields 参与求和的字段数组
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return Bean 字段求和
	 * */
	<T> Number[] searchSum(Class<T> beanClass, Map<String, Object> paraMap, String[] fields);
	
}
