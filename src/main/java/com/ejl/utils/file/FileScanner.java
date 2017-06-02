package com.ejl.utils.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {

	/**
	 * 递归查找文件
	 * 
	 * @param baseDirName
	 *            查找的文件夹路径
	 * @param targetFileName
	 *            需要查找的文件名
	 */
	public static List<File> findFiles(String baseDirName, String targetFileName) {
		/**
		 * 算法简述： 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件，
		 * 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。 队列不空，重复上述操作，队列为空，程序结束，返回结果。
		 */
		List<File> classFiles = new ArrayList<>();
		String tempName = null;
		// 判断目录是否存在
		File baseDir = new File(baseDirName);
		if (!baseDir.exists()) {
			throw new FileNotExistException(baseDirName);
		}
		if (!baseDir.isDirectory()) {
			throw new NotDirectoryException(baseDirName);
		}
		String[] filelist = baseDir.list();
		for (int i = 0; i < filelist.length; i++) {
			File readfile = new File(baseDirName + File.separatorChar + filelist[i]);
			if (!readfile.isDirectory()) {
				tempName = readfile.getName();
				if (FileScanner.wildcardMatch(targetFileName, tempName)) {
					classFiles.add(readfile.getAbsoluteFile());
				}
			} else if (readfile.isDirectory()) {
				classFiles.addAll(findFiles(baseDirName + File.separatorChar + filelist[i], targetFileName));
			}
		}

		return classFiles;
	}

	/**
	 * 通配符匹配
	 * 
	 * @param pattern
	 *            通配符模式
	 * @param str
	 *            待匹配的字符串
	 *            <a href="http://my.oschina.net/u/556800" target="_blank" rel=
	 *            "nofollow">@return</a> 匹配成功则返回true，否则返回false
	 */
	private static boolean wildcardMatch(String pattern, String str) {
		int patternLength = pattern.length();
		int strLength = str.length();
		int strIndex = 0;
		char ch;
		for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
			ch = pattern.charAt(patternIndex);
			if (ch == '*') {
				// 通配符星号*表示可以匹配任意多个字符
				while (strIndex < strLength) {
					if (wildcardMatch(pattern.substring(patternIndex + 1), str.substring(strIndex))) {
						return true;
					}
					strIndex++;
				}
			} else if (ch == '?') {
				// 通配符问号?表示匹配任意一个字符
				strIndex++;
				if (strIndex > strLength) {
					// 表示str中已经没有字符匹配?了。
					return false;
				}
			} else {
				if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
					return false;
				}
				strIndex++;
			}
		}
		return (strIndex == strLength);
	}

}
