package com.ejlchina.searcher.virtual;

import com.ejlchina.searcher.implement.SearchBeanMap;


/**
 * 虚拟参数处理器
 * @author Troy
 *
 */
public interface VirtualParamProcessor {

	
	
	SearchBeanMap process(SearchBeanMap searchBeanMap);
	
	
}
