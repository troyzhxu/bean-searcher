package com.ejl.searcher.implement.pagination;

import com.ejl.searcher.param.SearchParam;

/**
 * 分页解析器
 *  
 * @author Troy.Zhou
 *
 */
public interface PaginationResolver {

	
	/**
	 * 
	 * @param searchParam
	 * @param paraName 参数名
	 * @param paraValue 参数值
	 * @return true if resolved else false
	 */
	boolean resolve(SearchParam searchParam, String paraName, String paraValue);
	
	
	/**
	 * @return 最大条数的参数名
	 * */
	String getMaxParamName();
	
	/**
	 * @return 开始页
	 */
	public int getStartPage();
	
	
}
