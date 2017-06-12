package com.ejl.searcher.utils.file;

public class FileNotExistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1978272264062303687L;

	
	public FileNotExistException(String dirName) {
		super("文件[" + dirName + "]不存在！");
	}
}
