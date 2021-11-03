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
	 * @param meta 需要转换的字段元信息（非空）
	 * @param valueType 数据库值的类型（非空）
	 * @param targetType 目标类型（可空，为空时表示字段转换后将放入 Map 对象里，所以转换为什么类型都可以）
	 * @return 是否支持
	 */
	boolean supports(FieldMeta meta, Class<?> valueType, Class<?> targetType);

	/**
	 * 把 value 转换为 targetType 类型的数据
	 * @param meta 需要转换的字段元信息（非空）
	 * @param value 从数据库取出的待转换的值（非空）
	 * @param targetType 目标类型（可空，为空时表示字段转换后将放入 Map 对象里，所以转换为什么类型都可以）
	 * @return 转换目标值
	 * */
	Object convert(FieldMeta meta, Object value, Class<?> targetType);

}
