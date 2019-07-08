package com.ejlchina.searcher.implement.processor;

import com.ejlchina.searcher.param.Operator;


public interface ParamProcessor {

	
	
	Object[] upperCase(Object[] params);
	
	
	Object[] dateParams(Object[] params, Operator operator);
	
	
}
