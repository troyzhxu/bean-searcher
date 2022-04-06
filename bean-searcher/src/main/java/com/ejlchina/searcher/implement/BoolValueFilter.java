package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldMeta;
import com.ejlchina.searcher.BeanMeta;
import com.ejlchina.searcher.ParamFilter;
import com.ejlchina.searcher.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Bool 值过滤器
 * @author Troy.Zhou @ 2021-11-01
 * @since v3.0.0
 * */
public class BoolValueFilter implements ParamFilter {

	/**
	 * 参数名分割符
	 * v1.2.0之前默认值是下划线："_"，自 v1.2.0之后默认值更新为中划线："-"
	 */
	private String separator = "-";

	/**
	 * Boolean true 值参数后缀
	 */
	private String trueSuffix = "true";

	/**
	 * Boolean false 值参数后缀
	 */
	private String falseSuffix = "false";

	/**
	 * 忽略大小写参数名后缀
	 */
	private String ignoreCaseSuffix = "ic";

	/**
	 * False 参数值
	 */
	private String[] falseValues = new String[] { "0", "OFF", "FALSE", "N", "NO", "F" };


	@Override
	public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
		String[] fields = getBoolFieldList(beanMeta);
		String icSuffix = separator + ignoreCaseSuffix;
		Map<String, Object> map = new HashMap<>();
		for (Entry<String, Object> entry : paraMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (key.endsWith(icSuffix)) {
				map.put(key, toBoolean(value));
				continue;
			}
			String field = findField(fields, key);
			if (field == null) {
				// 不是 Bool 字段
				map.put(key, value);
				continue;
			}
			// 是 Bool 字段
			if (key.length() == field.length()) {
				map.put(field, toBoolean(value));
				continue;
			}
			map.put(field, key.endsWith(trueSuffix));
		}
		return map;
	}

	protected String[] getBoolFieldList(BeanMeta<?> beanMeta) {
		return beanMeta.getFieldMetas().stream()
				.filter(meta -> {
					Class<?> type = meta.getType();
					return type == boolean.class || type == Boolean.class;
				})
				.map(FieldMeta::getName)
				.toArray(String[]::new);
	}

	protected String findField(String[] fields, String key) {
		for (String field: fields) {
			if (key.startsWith(field)) {
				int fLen = field.length();
				if (key.length() == fLen) {
					return field;
				}
				String suffix = key.substring(fLen);
				if (!suffix.startsWith(separator)) {
					continue;
				}
				int len = suffix.length() - separator.length();
				if (len == falseSuffix.length() && suffix.endsWith(falseSuffix)
						|| len == trueSuffix.length() && suffix.endsWith(trueSuffix)) {
					return field;
				}
			}
		}
		return null;
	}

	protected Boolean toBoolean(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		if (value instanceof String) {
			String tv = ((String) value).trim();
			if (StringUtils.isBlank(tv)) {
				return null;
			}
			for (String fv: falseValues) {
				if (tv.equalsIgnoreCase(fv)) {
					return Boolean.FALSE;
				}
			}
		}
		if (value instanceof Number) {
			return ((Number) value).intValue() != 0;
		}
		return Boolean.TRUE;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = Objects.requireNonNull(separator);
	}

	public String getTrueSuffix() {
		return trueSuffix;
	}

	public void setTrueSuffix(String trueSuffix) {
		this.trueSuffix = Objects.requireNonNull(trueSuffix);
	}

	public String getFalseSuffix() {
		return falseSuffix;
	}

	public void setFalseSuffix(String falseSuffix) {
		this.falseSuffix = Objects.requireNonNull(falseSuffix);
	}

	public String getIgnoreCaseSuffix() {
		return ignoreCaseSuffix;
	}

	public void setIgnoreCaseSuffix(String ignoreCaseSuffix) {
		this.ignoreCaseSuffix = Objects.requireNonNull(ignoreCaseSuffix);
	}

	public String[] getFalseValues() {
		return falseValues;
	}

	public void setFalseValues(String[] falseValues) {
		this.falseValues = Objects.requireNonNull(falseValues);
	}

}
