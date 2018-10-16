package com.ejlchina.searcher.util;

public class StringUtils {


	public static boolean isBlank(String str) {
		if (str == null) {
			return true;
		}
		int len = str.length();
		if (len == 0) {
			return true;
		}
		for (int i = 0; i < len; i++) {
			switch (str.charAt(i)) {
			case ' ':
			case '\t':
			case '\n':
			case '\r':
				break;
			default:
				return false;
			}
		}
		return true;
	}
	
	public static String firstCharToUpperCase(String string) {
		if (string == null || string.length() == 0) {
			return string;
		}
		char first = string.charAt(0);
		first = (char) (first - 32);
		if (string.length() == 1) {
			return String.valueOf(first);
		}
		return String.valueOf(first) + string.substring(1);
	}
    
	
	/**
	 * 判断 src 里包含几个 target
	 * @param src
	 * @param from 开始计数下标（包含）
	 * @param to 结束计数下标（不包含）
	 * @param targets
	 * @return
	 */
	public static int containCount(String src, int from, int to, char[] targets) {
		int count = 0;
		if (src != null) {
			from = Math.max(from, 0);
			to = Math.min(to, src.length());
			for (int i = from; i < to; i ++) {
				char c = src.charAt(i);
				boolean contained = false;
				for (char target : targets) {
					if (c == target) {
						contained = true;
						break;
					}
				}
				if (contained) {
					count++;
				}
			}
		}
		return count;
	}
	
	
}
