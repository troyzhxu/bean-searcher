package com.ejlchina.searcher.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapUtils {

	/**
	 * 将一个 value 为 数组的 Map 对象，拉平为 value 为 单值的 Map 对象
	 */
	public static Map<String, Object> flat(Map<String, String[]> map) {
		Map<String, Object> newMap = new HashMap<>();
		for (Entry<String, String[]> entry: map.entrySet()) {
			String[] values =entry.getValue();
			if (values.length > 0) {
				newMap.put(entry.getKey(), values[0]);
			}
		}
		return newMap;
	}

	/**
	 * 返回一个 lambda Map 构造器
	 */
	public static MapBuilder builder() {
		return builder(new HashMap<>());
	}

	/**
	 * 返回一个 lambda Map 构造器
	 */
	public static MapBuilder flatBuilder(Map<String, String[]> map) {
		return new MapBuilder(flat(map));
	}

	/**
	 * 返回一个 lambda Map 构造器
	 */
	public static MapBuilder builder(Map<String, Object> map) {
		return new MapBuilder(map);
	}

}
