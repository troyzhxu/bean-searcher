package com.ejlchina.searcher.virtual;

import com.ejlchina.searcher.BeanMetadata;


/**
 * 虚拟参数处理器
 * @author Troy
 *
 */
public interface VirtualParamProcessor {

	
	
	BeanMetadata process(BeanMetadata beanMetadata);
	
	
}
