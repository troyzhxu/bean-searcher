package com.ejl.searcher;



/**
 * 检索器异常
 * 
 * @author Troy.Zhou @ 2017-03-20
 * */
public class SearcherException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9137870101657363708L;

	public SearcherException(String message) {
		super(message);
	}

	public SearcherException(String message, Throwable cause) {
		super(message, cause);
	}
	
	
}
