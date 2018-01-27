package com.ejlchina.searcher.implement.convertor;


/***
 * @author Troy.Zhou @ 2017-04-07
 * 
 * 数据库字段值转换接口
 * 用于把 数据库查出的字段值 转换为 用户bean 对用的属性类型
 */
public interface FieldConvertor {

	
	/**
	 * 把 value 转换为 fieldType 类型的数据
	 * @param value 从数据库查出的字段值
	 * @param fieldType 转换目标类型
	 * @return 转换目标值
	 * */
	public Object convert(Object value, Class<?> fieldType);

	
}
