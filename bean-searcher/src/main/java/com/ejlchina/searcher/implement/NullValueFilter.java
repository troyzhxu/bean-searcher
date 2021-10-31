package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.Metadata;
import com.ejlchina.searcher.ParamFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * 空值过滤器
 * 
 * */
public class NullValueFilter implements ParamFilter {

	private String[] nulls;
	
	@Override
	public <T> Map<String, Object> doFilter(Metadata<T> metadata, Map<String, Object> paraMap) {
		Map<String, Object> map = new HashMap<>();
		for (Entry<String, Object> entry : paraMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value != null) {
				if (isNotEmpty(value) || alwaysUse(metadata, key)) {
					map.put(key, value);
				}
			}
		}
		return map;
	}

	/**
	 * 当 key 是 nulls 中的一员时，是否仍然使用它，即不忽略它
	 * @return false 不使用这个参数， true: 仍然使用
	 */
	private <T> boolean alwaysUse(Metadata<T> metadata, String key) {
		return false;
	}

	protected boolean isNotEmpty(Object value) {
		for (String nil: nulls) {
			if (nil.equals(value)) {
				return false;
			}
		}
		return true;
	}

	public String[] getNulls() {
		return nulls;
	}

	public void setNulls(String[] nulls) {
		this.nulls = Objects.requireNonNull(nulls);
	}

}
