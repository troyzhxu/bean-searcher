package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.implement.DefaultBeanReflector;

import java.util.Arrays;
import java.util.Objects;

/**
 * [ String | Number | Boolean to Boolean | boolean] 字段转换器
 * 与 {@link DefaultBeanReflector } 配合使用
 * @author Troy.Zhou @ 2021-11-01
 * @since v3.0.0（v3.8.0 之前在 com.ejlchina.searcher.implement 包下）
 */
public class BoolFieldConvertor implements FieldConvertor.BFieldConvertor {

	private String[] falseValues = new String[] { "0", "OFF", "FALSE", "N", "NO", "F" };

	@Override
	public boolean supports(FieldMeta meta, Class<?> valueType) {
		if (Boolean.class == valueType || String.class == valueType || Number.class.isAssignableFrom(valueType)) {
			Class<?> targetType = meta.getType();
			return targetType == boolean.class || targetType == Boolean.class;
		}
		return false;
	}

	@Override
	public Object convert(FieldMeta meta, Object value) {
		if (value instanceof Boolean) {
			return value;
		}
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
