package com.ejl.searcher.dialect;


/**
 * 数据库方言
 *  
 * @author Troy.Zhou
 * 
 * */
public interface Dialect {

	
	/**
	 * 把字段 dbField 转换为大写
	 * @param builder sql builder
	 * @param dbField 数据库字段
	 */
	public void toUpperCase(StringBuilder builder, String dbField);
	
	
	/**
	 * 把字段 dbField 截取为 YYYY-MM-DD 格式字符串
	 * @param builder sql builder
	 * @param dbField 数据库字段
	 */
	public void truncateToDateStr(StringBuilder builder, String dbField);
	

}
