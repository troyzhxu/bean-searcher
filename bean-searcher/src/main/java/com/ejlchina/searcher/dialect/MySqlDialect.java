package com.ejlchina.searcher.dialect;


import com.ejlchina.searcher.SqlWrapper;
import com.ejlchina.searcher.param.Paging;

/**
 * MySql 方言实现
 *  
 * @author Troy.Zhou
 * 
 * */
public class MySqlDialect implements Dialect {

	@Override
	public SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
		SqlWrapper<Object> wrapper = new SqlWrapper<>();
		StringBuilder ret = new StringBuilder();
		ret.append(fieldSelectSql).append(fromWhereSql);
		if (paging != null) {
			ret.append(" limit ?, ?");
			wrapper.addPara(paging.getOffset());
			wrapper.addPara(paging.getSize());
		}
		wrapper.setSql(ret.toString());
		return wrapper;
	}

}
