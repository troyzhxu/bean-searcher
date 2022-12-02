package cn.zhxu.bs;

import cn.zhxu.bs.util.FieldFns;

import java.util.List;
import java.util.Map;

/**
 * Map 对象检索器接口
 * 根据 SearchBean 的 Class 和 检索参数，自动检索，数据以 Map 对象呈现
 * @author Troy.Zhou @ 2021-10-29
 * @since v3.0.0
 * */
public interface MapSearcher extends Searcher {

	/**
	 * 适合需要分页的查询
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数
	 * @return 总条数，Bean 数据列表
	 * */
	<T> SearchResult<Map<String, Object>> search(Class<T> beanClass, Map<String, Object> paraMap);

	/**
	 * 适合需要分页的查询
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数
	 * @param summaryField 统计字段
	 * @return 总条数，Bean 数据列表
	 * */
	<T> SearchResult<Map<String, Object>> search(Class<T> beanClass, Map<String, Object> paraMap, String summaryField);

	/**
	 * 适合需要分页的查询
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数
	 * @param summaryField 统计字段
	 * @return 总条数，Bean 数据列表
	 * */
	<T> SearchResult<Map<String, Object>> search(Class<T> beanClass, Map<String, Object> paraMap, FieldFns.FieldFn<T, ?> summaryField);

	/**
	 * 适合需要分页的查询
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数
	 * @param summaryFields 统计字段
	 * @return 总条数，Bean 数据列表
	 * */
	<T> SearchResult<Map<String, Object>> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields);

	/**
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return 满足条件的第一个Bean 
	 * */
	<T> Map<String, Object> searchFirst(Class<T> beanClass, Map<String, Object> paraMap);

	/**
	 * 适合不需要分页的查询
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return Bean 数据列表
	 * */
	<T> List<Map<String, Object>> searchList(Class<T> beanClass, Map<String, Object> paraMap);

	/**
	 * 检索满足条件的所有Bean，不支持偏移
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return Bean 数据列表
	 * */
	<T> List<Map<String, Object>> searchAll(Class<T> beanClass, Map<String, Object> paraMap);
	
}
