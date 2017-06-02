package com.ejl.searcher;


/**
 * @author Troy.Zhou @ 2017-03-20
 * 检索中间对象接口
 * */
public interface SearchTmpData {

	/**
	 * 根据 字段别名 取值
	 * */
	public Object get(String dbAlias);
	
}
