package com.ejl.utils.clazz;

public class ReadJarFileErrorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7496541272926376988L;


	public ReadJarFileErrorException(String jarName, Throwable cause) {
		super("JAR[" + jarName + "]", cause);
	}

	public ReadJarFileErrorException(String jarName) {
		super("JAR[" + jarName + "]");
	}


}
