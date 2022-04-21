package com.ejlchina.searcher;

import java.util.Collection;
import java.util.function.Function;

/**
 * Bean 反射器
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 * */
public interface BeanReflector {

	/**
	 * @param <T> bean 类型
	 * @param beanMeta 元信息
	 * @param fetchFields Bean 中需要反射赋值的字段
	 * @param valueGetter 数据库字段值获取器（根据字段别名获取）
	 * @return 反射的对象
	 */
	<T> T reflect(BeanMeta<T> beanMeta, Collection<String> fetchFields, Function<String, Object> valueGetter);

}
