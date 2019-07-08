package com.ejlchina.searcher;

import com.ejlchina.searcher.beanmap.SearchBeanMap;
import com.ejlchina.searcher.param.SearchParam;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 检索SQL解析接口
 * 
 * */
public interface SearchSqlResolver {


	public SearchSql resolve(SearchBeanMap searchBeanMap, SearchParam searchParam);
	
	
}
