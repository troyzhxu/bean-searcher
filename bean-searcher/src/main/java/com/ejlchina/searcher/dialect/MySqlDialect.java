package com.ejlchina.searcher.dialect;


import com.ejlchina.searcher.param.Paging;

/**
 * MySql 方言实现
 *  
 * @author Troy.Zhou
 * 
 * */
public class MySqlDialect implements Dialect {

	@Override
	public void toUpperCase(StringBuilder builder, String dbField) {
		builder.append("upper").append("(").append(dbField).append(")");
	}
	
	@Override
	public PaginateSql forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
		PaginateSql paginateSql = new PaginateSql();
		StringBuilder ret = new StringBuilder();
		ret.append(fieldSelectSql).append(fromWhereSql);
		if (paging != null) {
			ret.append(" limit ?, ?");
			paginateSql.addParam(paging.getOffset());
			paginateSql.addParam(paging.getSize());
		}
		paginateSql.setSql(ret.toString());
		return paginateSql;
	}

}
