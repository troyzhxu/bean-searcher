package com.ejlchina.searcher.util;

public class StringUtils {

	/**
	 * @param str 字符串
	 * @return 是否不空
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	/**
	 * @param str 字符串
	 * @return 是否为空
	 */
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

	/**
	 * 首字母大写
	 * @param str 字符串
	 * @return String
	 */
	public static String firstCharToUpperCase(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		char first = str.charAt(0);
		if (first >= 'a' && first <= 'z') {
			first = (char) (first - 32);
			if (str.length() == 1) {
				return String.valueOf(first);
			}
			return first + str.substring(1);
		}
		return str;
	}

	/**
	 * 首字母小写
	 * @param str 字符串
	 * @return String
	 */
	public static String firstCharToLoweCase(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		char first = str.charAt(0);
		if (first >= 'A' && first <= 'Z') {
			first = (char) (first + 32);
			if (str.length() == 1) {
				return String.valueOf(first);
			}
			return first + str.substring(1);
		}
		return str;
	}

	/**
	 * 查找 src 里包含几个 target
	 * @param src 源字符串
	 * @param from 开始计数下标（包含）
	 * @param to 结束计数下标（不包含）
	 * @param targets 目标字符
	 * @return 个数
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

	/**
	 * 驼峰风格风格转连字符风格
	 * @param src 驼峰字符串
	 * @param hyphenation 连字符
	 * @return 连字符风格字符串
	 */
	public static String toHyphenation(String src, String hyphenation) {
		StringBuilder sb = new StringBuilder(src);
		int temp = 0;//定位
		for(int i = 0; i < src.length(); i++){
			if(Character.isUpperCase(src.charAt(i))){
				sb.insert(i + temp, hyphenation);
				temp += 1;
			}
		}
		return sb.toString().toLowerCase();
	}

	/**
	 * 驼峰风格风格转下划线风格
	 * @param src 驼峰字符串
	 * @return 下划风格字符串
	 */
	public static String toUnderline(String src) {
		return toHyphenation(src, "_");
	}
	
}
