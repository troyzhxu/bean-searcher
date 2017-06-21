package com.ejl.searcher.dialect;


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
	public void truncateToDateStr(StringBuilder builder, String dbField) {
		builder.append("date_format(").append(dbField).append(", '%Y-%m-%d')");
	}

	@Override
	public void truncateToDateMinuteStr(StringBuilder builder, String dbField) {
		builder.append("date_format(").append(dbField).append(", '%Y-%m-%d %H:%i')");
	}

	@Override
	public void truncateToDateSecondStr(StringBuilder builder, String dbField) {
		builder.append("date_format(").append(dbField).append(", '%Y-%m-%d %H:%i:%s')");
	}
	
	@Override
	public PaginateSql forPaginate(String fieldSelectSql, String fromWhereSql, Integer max, Long offset) {
		PaginateSql paginateSql = new PaginateSql();
		StringBuilder ret = new StringBuilder();
		ret.append(fieldSelectSql).append(" ").append(fromWhereSql);
		if (max != null) {
			if (offset == null) {
				ret.append(" limit ?");
			} else {
				ret.append(" limit ?, ?");
				paginateSql.addParam(offset);
			}
			paginateSql.addParam(max);
		}
		paginateSql.setSql(ret.toString());
		return paginateSql;
	}


}
