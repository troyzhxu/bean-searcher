package com.ejlchina.searcher.implement;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.SearchException;

/**
 * 默认数据库字段值转换器
 *  
 * @author Troy.Zhou @ 2017-04-07
 * 
 */
public class DefaultFieldConvertor implements FieldConvertor {

	public static final String[] DEFAULT_TRUES = {  "1", "TRUE", "Y", "T", "YES", "ON" };
	public static final String[] DEFAULT_FALSES = { "0", "FALSE", "N", "F", "NO", "OFF" };
	
	private boolean ignoreCase = true;
	
	private String[] trues = DEFAULT_TRUES;
	
	private String[] falses = DEFAULT_FALSES;


	@Override
	public boolean supports(Class<?> valueType, Class<?> targetType) {
		return true;
	}

	@Override
	public Object convert(Object value, Class<?> targetType) {
		if (value == null) {
			return value;
		}
		Class<?> clazz = value.getClass();
		// 有继承关系
		if (targetType.isAssignableFrom(clazz)) {
			return value;
		}
		String strValue = value.toString();
		try {
			if (targetType == int.class || targetType == Integer.class) {
				return Integer.valueOf(strValue);
			}
			if (targetType == long.class || targetType == Long.class) {
				return Long.valueOf(strValue);
			}
			if (targetType == float.class || targetType == Float.class) {
				return Float.valueOf(strValue);
			}
			if (targetType == double.class || targetType == Double.class) {
				return Double.valueOf(strValue);
			}
		} catch (Exception e) {
			throw new SearchException("不能把【" + value.getClass() + "】转换为【" + targetType + "】类型！", e);
		}
		if (targetType == boolean.class || targetType == Boolean.class) {
			if (ignoreCase) {
				strValue = strValue.toUpperCase();
			}
			for (String t: trues) {
				if (t.equals(strValue)) {
					return Boolean.TRUE;
				}
			}
			for (String f: falses) {
				if (f.equals(strValue)) {
					return Boolean.FALSE;
				}
			}
			throw new SearchException("不能把【" + value.getClass() + "】转换为boolean值！");
		}
		if (targetType == BigDecimal.class) {
			return new BigDecimal(strValue);
		}
		if (targetType == BigInteger.class) {
			return new BigInteger(strValue);
		}
		throw new SearchException("不能把【" + value.getClass() + "】转换为【" + targetType + "】类型！");
	}


	public String[] getFalses() {
		return falses;
	}


	public void setFalses(String[] falses) {
		this.falses = falses;
	}


	public String[] getTrues() {
		return trues;
	}


	public void setTrues(String[] trues) {
		this.trues = trues;
	}


	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
	
}
