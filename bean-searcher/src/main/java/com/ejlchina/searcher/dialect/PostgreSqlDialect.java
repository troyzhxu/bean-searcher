package com.ejlchina.searcher.dialect;

import com.ejlchina.searcher.param.PageParam;

public class PostgreSqlDialect implements Dialect {

	@Override
	public void toUpperCase(StringBuilder builder, String dbField) {
		builder.append("upper").append("(").append(dbField).append(")");
	}

	@Override
	public void truncateToDateStr(StringBuilder builder, String dbField) {
		// TODO Auto-generated method stub

	}

	@Override
	public void truncateToDateMinuteStr(StringBuilder builder, String dbField) {
		// TODO Auto-generated method stub

	}

	@Override
	public void truncateToDateSecondStr(StringBuilder builder, String dbField) {
		// TODO Auto-generated method stub

	}

	@Override
	public PaginateSql forPaginate(String fieldSelectSql, String fromWhereSql, PageParam limit) {
		// TODO Auto-generated method stub
		return null;
	}

}
