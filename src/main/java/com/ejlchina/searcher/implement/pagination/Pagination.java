package com.ejlchina.searcher.implement.pagination;

import com.ejlchina.searcher.param.SearchParam;

/**
 * 分页解析器
 *  
 * @author Troy.Zhou
 *
 */
public interface Pagination {

	
	/**
	 * @param searchParam 检索参数
	 * @param paraName 参数名
	 * @param paraValue 参数值
	 * @return true if paginated else false
	 */
	boolean paginate(SearchParam searchParam, String paraName, Object paraValue);
	
	
	/**
	 * @return 最大条数的参数名
	 * */
	String getMaxParamName();
	
	/**
	 * @return 开始页
	 */
	public int getStartPage();
	
	
}
