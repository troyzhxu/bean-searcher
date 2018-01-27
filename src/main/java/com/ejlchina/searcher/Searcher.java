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
	 * @param prefix 检索参数前缀
	 * @return 总条数，Bean 数据列表
	 * */
	<T> SearchResult<T> search(Class<T> beanClass, Map<String, String> paraMap, String prefix);
	
	/**
	 * 适合需要分页的查询
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数
	 * @return 总条数，Bean 数据列表
	 * */
	<T> SearchResult<T> search(Class<T> beanClass, Map<String, String> paraMap);
	
	/**
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @param prefix 检索参数前缀
	 * @return 满足条件的第一个Bean 
	 * */
	<T> T searchFirst(Class<T> beanClass, Map<String, String> paraMap, String prefix);
	
	/**
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return 满足条件的第一个Bean 
	 * */
	<T> T searchFirst(Class<T> beanClass, Map<String, String> paraMap);
	
	
	/**
	 * 适合不需要分页的查询
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @param prefix 检索参数前缀
	 * @return Bean 数据列表
	 * */
	<T> List<T> searchList(Class<T> beanClass, Map<String, String> paraMap, String prefix);
	
	/**
	 * 适合不需要分页的查询
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return Bean 数据列表
	 * */
	<T> List<T> searchList(Class<T> beanClass, Map<String, String> paraMap);
	
	/**
	 * 检索满足条件的所有Bean，不支持偏移
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @param prefix 检索参数前缀
	 * @return Bean 数据列表
	 * */
	<T> List<T> searchAll(Class<T> beanClass, Map<String, String> paraMap, String prefix);
	
	/**
	 * 检索满足条件的所有Bean，不支持偏移
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return Bean 数据列表
	 * */
	<T> List<T> searchAll(Class<T> beanClass, Map<String, String> paraMap);
	
	
	/**
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @param prefix 检索参数前缀
	 * @return Bean 数据个数
	 * */
	<T> Number searchCount(Class<T> beanClass, Map<String, String> paraMap, String prefix);
	
	/**
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return Bean 数据个数
	 * */
	<T> Number searchCount(Class<T> beanClass, Map<String, String> paraMap);
	
	
}
