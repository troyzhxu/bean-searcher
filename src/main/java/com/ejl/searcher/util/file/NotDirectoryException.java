package com.ejl.searcher.util.file;

/**
 * 不是一个目录异常
 * @author Troy.Zhou
 *
 */
public class NotDirectoryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5171021232381615982L;

	
	public NotDirectoryException(String dirName, Throwable cause) {
		super("【" + dirName + "】不是一个文件夹", cause);
	}

	public NotDirectoryException(String dirName) {
		super("【" + dirName + "】不是一个文件夹");
	}
	
	
}
