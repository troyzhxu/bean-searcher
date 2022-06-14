package com.ejlchina.searcher;

import com.ejlchina.searcher.bean.DbType;
import com.ejlchina.searcher.param.FetchType;

import java.util.Map;

/**
 * 请求参数解析器接口
 * 
 * @author Troy.Zhou @ 2017-03-20
 * */
public interface ParamResolver {

	/**
	 * @param beanMeta 元数据
	 * @param fetchType Fetch 类型
	 * @param paraMap 原始检索参数
	 * @return SearchParam
	 * */
	SearchParam resolve(BeanMeta<?> beanMeta, FetchType fetchType, Map<String, Object> paraMap);

	/**
	 * 参数值转换器
	 * @since v3.8.0
	 */
	interface Convertor {

		/**
		 * @param dbType 需转换的目标类型
		 * @param valueType 值类型
		 * @return 是否支持
		 */
		boolean supports(DbType dbType, Class<?> valueType);

		/**
		 * @param dbType 目标类型
		 * @param value 待转换的值
		 * @return 转换后的值
		 */
		Object convert(DbType dbType, Object value);

	}

}
