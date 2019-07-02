package com.ejlchina.searcher.util;

public class ObjectUtils {


	public static Integer toInt(Object value) {
		if (value != null) {
			if (value instanceof Number) {
				return ((Number) value).intValue();
			}
			if (value instanceof String) {
				return Integer.valueOf((String) value);
			}
		}
		return null;
	}

	public static Long toLong(Object value) {
		if (value != null) {
			if (value instanceof Number) {
				return ((Number) value).longValue();
			}
			if (value instanceof String) {
				return Long.valueOf((String) value);
			}
		}
		return null;
	}


}
