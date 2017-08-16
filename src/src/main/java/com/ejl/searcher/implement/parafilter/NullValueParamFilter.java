package com.ejl.searcher.implement.parafilter;

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
	public Map<String, String> doFilte(Map<String, String> paraMap) {
		Map<String, String> map = new HashMap<>();
		for (Entry<String, String> entry : paraMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
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
		return map;
	}

}
