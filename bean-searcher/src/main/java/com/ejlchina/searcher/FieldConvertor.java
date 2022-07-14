package com.ejlchina.searcher;

import com.ejlchina.searcher.implement.DefaultBeanReflector;
import com.ejlchina.searcher.implement.DefaultMapSearcher;

/**
 * @author Troy.Zhou @ 2017-04-07
 * 
 * 数据库字段值转换接口
 * 用于把 数据库查出的字段值 型转为 另外一种值
 * 为提高字段转换效能，v3.1.0 把字段转换器拆为两类：{@link BFieldConvertor } 与 {@link MFieldConvertor }
 * 以降低 {@link #supports(FieldMeta, Class)} 方法判断次数
 */
public interface FieldConvertor extends Convertor {

	/**
	 * @since v3.0.0
	 * 是否支持 valueType 转成 targetType
	 * v3.2.0 后移除冗余参数 targetType，该参数可通过 meta.getType() 获取
	 * @param meta 需要转换的字段元信息（非空）
	 * @param valueType 数据库值的类型（非空）
	 * @return 是否支持
	 */
	boolean supports(FieldMeta meta, Class<?> valueType);

	/**
	 * 把 value 转换为 targetType 类型的数据
	 * v3.2.0 后移除冗余参数 targetType，该参数可通过 meta.getType() 获取
	 * @param meta 需要转换的字段元信息（非空）
	 * @param value 从数据库取出的待转换的值（非空）
	 * @return 转换目标值
	 * */
	default Object convert(FieldMeta meta, Object value) {
		return value;
	}

	/**
	 * 只在 {@link DefaultBeanReflector } 中使用
	 * @author Troy.Zhou @ 2021-11-09
	 * @since v3.1.0
	 */
	interface BFieldConvertor extends FieldConvertor { }

	/**
	 * 只在 {@link DefaultMapSearcher } 中使用
	 * @author Troy.Zhou @ 2021-11-09
	 * @since v3.1.0
	 */
	interface MFieldConvertor extends FieldConvertor { }

}
