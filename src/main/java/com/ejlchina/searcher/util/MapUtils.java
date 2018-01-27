package com.ejlchina.searcher.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapUtils {

	
	public static Map<String, String> flat(Map<String, String[]> map) {
		Map<String, String> newMap = new HashMap<String, String>();
		for (Entry<String, String[]> entry: map.entrySet()) {
			String[] values =entry.getValue();
			if (values.length > 0) {
				newMap.put(entry.getKey(), values[0]);
			}
		}
		return newMap;
	}
	
	
}
