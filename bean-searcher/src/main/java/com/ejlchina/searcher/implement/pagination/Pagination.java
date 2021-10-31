package com.ejlchina.searcher.implement.pagination;

import com.ejlchina.searcher.SearchParam;
import com.ejlchina.searcher.param.PageParam;

import java.util.Map;

/**
 * 分页解析器
 *  
 * @author Troy.Zhou
 *
 */
public interface Pagination {

	/**
	 * @param paraMap 检索参数
	 * @return 分页信息
	 */
	PageParam paginate(Map<String, Object> paraMap);
	
	
	/**
	 * @return 最大条数的参数名
	 * */
	String getSizeParamName();
	
	/**
	 * @return 开始页
	 */
	int getStartPage();
	
	
}
