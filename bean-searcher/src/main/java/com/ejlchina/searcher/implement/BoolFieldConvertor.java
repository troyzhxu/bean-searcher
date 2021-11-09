package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;

import java.util.Arrays;
import java.util.Objects;

/**
 * [字符串 | 数字 to 布尔] 字段转换器
 * @author Troy.Zhou @ 2021-11-01
 * @since v3.0.0
 */
public class BoolFieldConvertor implements FieldConvertor {

	private String[] falseValues = new String[] { "0", "OFF", "FALSE", "N", "NO", "F" };

	@Override
	public boolean supports(FieldMeta meta, Class<?> valueType, Class<?> targetType) {
		return (valueType == String.class || Number.class.isAssignableFrom(valueType)) && (targetType == boolean.class || targetType == Boolean.class);
	}

	@Override
	public Object convert(FieldMeta meta, Object value, Class<?> targetType) {
		if (value instanceof String) {
			String bool = (String) value;
			for (String t: falseValues) {
				if (t.equalsIgnoreCase(bool)) {
					return Boolean.FALSE;
				}
			}
			return Boolean.TRUE;
		}
		return ((Number) value).intValue() != 0;
	}

	public String[] getFalseValues() {
		return falseValues;
	}

	public void setFalseValues(String[] falseValues) {
		this.falseValues = Objects.requireNonNull(falseValues);
	}

	/**
	 * 追加假值
	 * @param falseValues 假值
	 * @since v3.1.0
	 */
	public void addFalseValues(String[] falseValues) {
		int newLength = falseValues.length + this.falseValues.length;
		this.falseValues = Arrays.copyOf(this.falseValues, newLength);
		System.arraycopy(falseValues, 0, this.falseValues, this.falseValues.length, falseValues.length);
	}

}
