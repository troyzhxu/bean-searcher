package com.ejlchina.searcher;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 查询结果解析接口
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 * */
public interface SearchResultResolver {

	/**
	 * @param <T> bean 类型
	 * @param convertInfo 转换信息
	 * @param dataListResult 数据集
	 * @return 检索结果
	 */
	<T> List<T> resolve(SearchResultConvertInfo<T> convertInfo, ResultSet dataListResult) throws SQLException;

}
