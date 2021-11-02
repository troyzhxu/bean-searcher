package com.ejlchina.searcher.bean;


import java.util.Map;

/**
 * 参数感知接口，SearchBean 的可选实现接口，
 * 当 SearchBean 实现这个接口时，可以在 afterAssembly 方法里添加 bean 装配完之后的自定义逻辑
 * 并且可以感知到检索时的参数
 *
 * @author Troy.Zhou @ 2021-11-01
 * 
 * */
public interface ParamAware {

	/**
	 * 装配之后
	 * @param paraMap 检索参数
	 * */
	void afterAssembly(Map<String, Object> paraMap);
	
}
