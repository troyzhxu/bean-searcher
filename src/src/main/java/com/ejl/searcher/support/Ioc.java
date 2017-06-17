package com.ejl.searcher.support;

import java.util.HashMap;
import java.util.Map;

/**
 * 简易的Ioc容器，用于管理单例对象
 * 
 * @author Troy.Zhou
 *
 */
public class Ioc {

	private static Map<String, Object> nameBeanPool = new HashMap<>();
	
	private static Map<Class<?>, Object> classBeanPool = new HashMap<>();
	
	/**
	 * 按名称向容器添加实例
	 * @param name 实例名称
	 * @param bean 实例对象
	 */
	public static void add(String name, Object bean) {
		nameBeanPool.put(name, bean);
	}
	
	/**
	 * 按类型向容器添加实例
	 * @param clazz 实例类型
	 * @param bean 实例对象
	 */
	public static void add(Class<?> clazz, Object bean) {
		classBeanPool.put(clazz, bean);
	}
	
	/**
	 * 按名称从容器获取实例
	 * @param name 实例名称
	 * @return 实例对象
	 */
	public static Object get(String name) {
		return nameBeanPool.get(name);
	}
	
	/**
	 * 按类型从容器获取实例
	 * @param clazz 实例类型
	 * @return 实例对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> clazz) {
		return (T) classBeanPool.get(clazz);
	}
	
	
}
