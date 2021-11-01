package com.ejlchina.searcher;


/***
 * @author Troy.Zhou @ 2017-04-07
 * 
 * 数据库字段值转换接口
 * 用于把 数据库查出的字段值 转换为 用户bean 对用的属性类型
 */
public interface FieldConvertor {

	/**
	 * @since v3.0.0
	 * 是否支持 valueType 转成 targetType
	 * @param valueType 数据库值的类型
	 * @param targetType 目标类型
	 * @return 是否支持
	 */
	boolean supports(Class<?> valueType, Class<?> targetType);

	/**
	 * 把 value 转换为 targetType 类型的数据
	 * @param value 从数据库查出的字段值（非空）
	 * @param targetType 转换目标类型
	 * @return 转换目标值
	 * */
	Object convert(Object value, Class<?> targetType);

}
