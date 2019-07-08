package com.ejlchina.searcher.implement.parafilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 去除空值
 * 
 * */
public class NullValueParamFilter implements ParamFilter {

	
	private String[] nullValues;
	
	
	@Override
	public Map<String, Object> doFilte(Map<String, Object> paraMap) {
		Map<String, Object> map = new HashMap<>();
		for (Entry<String, Object> entry : paraMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value != null) {
				boolean isNotEmpty = true;
				for (String emptyValue : nullValues) {
					if (emptyValue.equals(value)) {
						isNotEmpty = false;
						break;
					}
				}
				if (isNotEmpty) {
					map.put(key, value);
				}
			}
		}
		return map;
	}


	public String[] getNullValues() {
		return nullValues;
	}


	public void setNullValues(String[] nullValues) {
		this.nullValues = nullValues;
	}

}
