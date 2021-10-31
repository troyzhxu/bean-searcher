package com.ejlchina.searcher;



/**
 * 检索器异常
 * 
 * @author Troy.Zhou @ 2017-03-20
 * */
public class SearchException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9137870101657363708L;

	public SearchException(String message) {
		super(message);
	}

	public SearchException(String message, Throwable cause) {
		super(message, cause);
	}
	
	
}
