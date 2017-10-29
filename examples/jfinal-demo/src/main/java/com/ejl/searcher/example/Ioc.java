package com.ejl.searcher.example;

import java.util.HashMap;
import java.util.Map;

public class Ioc {

	private static Map<Class<?>, Object> pool = new HashMap<>();
	
	
	public static void put(Class<?> clazz, Object bean) {
		pool.put(clazz, bean);
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> clazz) {
		return (T) pool.get(clazz);
	}
	
	
}
