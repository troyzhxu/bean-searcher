package com.ejl.searcher.util.file;


/**
 * 文件不存在异常
 * @author Troy.Zhou
 *
 */
public class FileNotExistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1978272264062303687L;

	
	public FileNotExistException(String dirName) {
		super("文件[" + dirName + "]不存在！");
	}
}
