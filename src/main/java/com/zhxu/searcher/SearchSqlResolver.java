package com.zhxu.searcher;

import com.zhxu.searcher.beanmap.SearchBeanMap;
import com.zhxu.searcher.param.SearchParam;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 检索SQL解析接口
 * 
 * */
public interface SearchSqlResolver {


	public SearchSql resolve(SearchBeanMap searchBeanMap, SearchParam searchParam);
	
	
}
