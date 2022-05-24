package com.ejlchina.searcher.dialect;


import com.ejlchina.searcher.SqlWrapper;
import com.ejlchina.searcher.param.Paging;

/**
 * SqlServer (v2012+) 方言实现
 * @author Troy.Zhou @ 2022-05-22
 * @since v3.7.0
 * */
public class SqlServerDialect implements Dialect {
	
	@Override
	public SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
		SqlWrapper<Object> wrapper = new SqlWrapper<>();
		StringBuilder ret = new StringBuilder();
		ret.append(fieldSelectSql).append(fromWhereSql);
		if (paging != null) {
			ret.append(" offset ? rows fetch next ? rows only");
			wrapper.addPara(paging.getOffset());
			wrapper.addPara(paging.getSize());
		}
		wrapper.setSql(ret.toString());
		return wrapper;
	}

}
