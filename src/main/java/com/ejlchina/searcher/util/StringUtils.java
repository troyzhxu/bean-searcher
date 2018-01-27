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
    
}
