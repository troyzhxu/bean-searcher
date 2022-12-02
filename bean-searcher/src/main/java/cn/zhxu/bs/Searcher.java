package cn.zhxu.bs;

import cn.zhxu.bs.util.FieldFns;

import java.util.Map;

/**
 * 检索器接口
 * 根据 SearchBean 的 Class 和 检索参数，自动检索数据
 * 它有两个子接口：{@link MapSearcher } 与 {@link BeanSearcher }
 *
 * @author Troy.Zhou @ 2017-03-20
 * @since v1.0.0
 * */
public interface Searcher {

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
	 * @param field 参与求和的字段
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return 字段求和统计
	 * */
	<T> Number searchSum(Class<T> beanClass, Map<String, Object> paraMap, FieldFns.FieldFn<T, ?> field);

	/**
	 * @param <T> bean 类型
	 * @param beanClass 要检索的 bean 类型
	 * @param fields 参与求和的字段数组
	 * @param paraMap 检索参数（包括排序分页参数）
	 * @return 字段求和统计
	 * */
	<T> Number[] searchSum(Class<T> beanClass, Map<String, Object> paraMap, String[] fields);

}
