package com.ejl.searcher.dialect;


/**
 * @author Troy.Zhou
 * 
 * 数据库方言
 * 
 * */
public interface Dialect {

	
	/**
	 * 把字段 dbField 转换为大写
	 * */
	public void toUpperCase(StringBuilder builder, String dbField);
	
	/**
	 * 把字段 dbField 截取为 YYYY-MM-DD 格式字符串
	 * */
	public void truncateToDateStr(StringBuilder builder, String dbField);
	

}
