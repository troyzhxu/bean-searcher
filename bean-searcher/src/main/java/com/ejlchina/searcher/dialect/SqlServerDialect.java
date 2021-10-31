package com.ejlchina.searcher.dialect;

import com.ejlchina.searcher.param.PageParam;

/**
 * Sql Server 方言实现
 * 
 * @author Troy.Zhou
 *
 */
public class SqlServerDialect implements Dialect {

	@Override
	public void toUpperCase(StringBuilder builder, String dbField) {
		builder.append("upper").append("(").append(dbField).append(")");
	}

	@Override
	public void truncateToDateStr(StringBuilder builder, String dbField) {
		builder.append("convert(varchar(100), ").append(dbField).append(", 23)");
	}

	@Override
	public void truncateToDateMinuteStr(StringBuilder builder, String dbField) {
		builder.append("left(convert(varchar(100), ").append(dbField).append(", 20), 16)");
	}

	@Override
	public void truncateToDateSecondStr(StringBuilder builder, String dbField) {
		builder.append("convert(varchar(100), ").append(dbField).append(", 20)");
	}

	@Override
	public PaginateSql forPaginate(String fieldSelectSql, String fromWhereSql, PageParam limit) {

		PaginateSql paginateSql = new PaginateSql();
	
		
		
		StringBuilder ret = new StringBuilder();
		
		ret.append("select * from (select row_number() over (order by tempcolumn) temprownumber, * from ");
		
//		ret.append("(select top ").append(offset + max).append(" tempcolumn=0,");
		
		// (?i) 不区分大小写
		ret.append(fieldSelectSql.replaceFirst("(?i)select", "")).append(fromWhereSql);
		
//		ret.append(") vip) mvp where temprownumber > ").append(offset);

		/**
		select * 
		  from (select row_number() over (order by tempcolumn) temprownumber, 
		  	   		   * 
		  	   	  from (select top 10 tempcolumn = 0, ...
		  	   	          from ...) vip) mvp
		 where temprownumber > 0
		
		
		
		
		*/
		paginateSql.setSql(ret.toString());
		
		return paginateSql;
	}

}
