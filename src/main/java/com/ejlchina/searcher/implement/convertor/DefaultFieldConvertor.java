package com.ejlchina.searcher.implement.convertor;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.ejlchina.searcher.SearcherException;

/**
 * 默认数据库字段值转换器
 *  
 * @author Troy.Zhou @ 2017-04-07
 * 
 */
public class DefaultFieldConvertor implements FieldConvertor {

	@Override
	public Object convert(Object value, Class<?> fieldType) {
		if (value == null) {
			return value;
		}
		Class<?> clazz = value.getClass();
		// 有继承关系
		if (fieldType.isAssignableFrom(clazz)) {
			return value;
		}
		String strValue = value.toString();
		try {
			if (fieldType == int.class || fieldType == Integer.class) {
				return Integer.valueOf(strValue);
			}
			if (fieldType == long.class || fieldType == Long.class) {
				return Long.valueOf(strValue);
			}
			if (fieldType == float.class || fieldType == Float.class) {
				return Float.valueOf(strValue);
			}
			if (fieldType == double.class || fieldType == Double.class) {
				return Double.valueOf(strValue);
			}
		} catch (Exception e) {
			throw new SearcherException("不能把【" + value.getClass() + "】转换为【" + fieldType + "】类型！", e);
		}
		if (fieldType == boolean.class || fieldType == Boolean.class) {
			strValue = strValue.toUpperCase();
			if ("0".equals(strValue) || "FALSE".equals(strValue) || "N".equals(strValue) || "F".equals(strValue)
					|| "NO".equals(strValue)) {
				return Boolean.FALSE;
			}
			if ("1".equals(strValue) || "TRUE".equals(strValue) || "Y".equals(strValue) || "T".equals(strValue)
					|| "YES".equals(strValue)) {
				return Boolean.TRUE;
			}
			throw new SearcherException("不能把【" + value.getClass() + "】转换为boolean值！");
		}
		if (fieldType == BigDecimal.class) {
			return new BigDecimal(strValue);
		}
		if (fieldType == BigInteger.class) {
			return new BigInteger(strValue);
		}
		throw new SearcherException("不能把【" + value.getClass() + "】转换为【" + fieldType + "】类型！");
	}

}
