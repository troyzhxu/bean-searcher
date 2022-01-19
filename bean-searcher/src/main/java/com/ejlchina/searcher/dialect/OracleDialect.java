package com.ejlchina.searcher.dialect;

import com.ejlchina.searcher.param.Paging;

/**
 * Oracle 方言实现
 * 
 * @author Troy.Zhou
 * @since V1.1.1
 * */
public class OracleDialect implements Dialect {

	@Override
	public String getType() {
		return "Oracle";
	}

	@Override
	public void toUpperCase(StringBuilder builder, String dbField) {
		builder.append("upper").append("(").append(dbField).append(")");
	}

	@Override
	public PaginateSql forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
		PaginateSql paginateSql = new PaginateSql();
		if (paging == null) {
			paginateSql.setSql(fieldSelectSql + fromWhereSql);
			return paginateSql;
		}
		String rowAlias = "row_";
		while (fromWhereSql.contains(rowAlias)) {
			rowAlias += "_";
		}
		StringBuilder builder = new StringBuilder();

		String tableAlias = "table_";
		while (fromWhereSql.contains(tableAlias)) {
			tableAlias += "_";
		}

		String rownumAlias = "rownum_";
		while (fieldSelectSql.contains(rownumAlias)) {
			rownumAlias += "_";
		}

		builder.append("select * from (select ").append(rowAlias).append(".*, rownum ").append(rownumAlias);

		builder.append(" from (").append(fieldSelectSql).append(fromWhereSql);

		builder.append(") ").append(rowAlias).append(" where rownum <= ?) ").append(tableAlias);

		builder.append(" where ").append(tableAlias).append(".").append(rownumAlias).append(" > ?");

		int size = paging.getSize();
		long offset = paging.getOffset();

		paginateSql.addParam(offset + size);
		paginateSql.addParam(offset);


		paginateSql.setSql(builder.toString());
		
		return paginateSql;
	}

}
