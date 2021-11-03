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
	 * @param valueType 数据库值的类型（非空）
	 * @param targetType 目标类型（可空，为空时表示：字段转换后将放入 Map 对象里）
	 * @return 是否支持
	 */
	boolean supports(Class<?> valueType, Class<?> targetType);

	/**
	 * 把 value 转换为 targetType 类型的数据
	 * @param value 从数据库查出的字段值（非空）
	 * @param targetType 转换目标类型（可空，为空时表示：字段转换后将放入 Map 对象里）
	 * @return 转换目标值
	 * */
	Object convert(Object value, Class<?> targetType);

	/**
	 * 进一步指定该转换器支持哪些 Bean 和 字段
	 * @author Troy.Zhou @ 2021-11-03
	 * @since v3.0.0
	 */
	interface Selector {

		/**
		 * 是否支持某个 Bean 的某个字段
		 * @param fieldMeta 需要转换的字段元信息（非空）
		 * @return 是否支持
		 */
		boolean supports(FieldMeta fieldMeta);

	}

}
