package com.ejl.utils;

public class StrUtils {

	/**
	 * 字符串为 null 或者内部字符全部为 ' ' '\t' '\n' '\r' 这四类字符时返回 true
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
			// case '\b':
			// case '\f':
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
	
    public static String join(String join, String[] strAry){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < strAry.length; i++){
            if(i == (strAry.length-1)) {
                sb.append(strAry[i]);
            } else{
                sb.append(strAry[i]).append(join);
            }
        }
        return new String(sb);
    }
	
	public static String trimJoin(String[] stringArray, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<stringArray.length; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(stringArray[i].trim());
		}
		return sb.toString();
	}
    
    
    public static String getLastByToken(String src, String token) {
    	if (src == null) {
    		throw new IllegalArgumentException("src 不能为 null");
    	}
    	String[] splits = src.split(token);
    	return splits[splits.length - 1];
    }
    
}
