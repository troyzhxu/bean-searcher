package com.ejlchina.searcher.dialect;

/**
 * Oracle 方言实现
 * 
 * @author Troy.Zhou
 * @since V1.1.1
 * */
public class OracleDialect implements Dialect {

	@Override
	public void toUpperCase(StringBuilder builder, String dbField) {
		builder.append("upper").append("(").append(dbField).append(")");
	}

	@Override
	public void truncateToDateStr(StringBuilder builder, String dbField) {
		builder.append("to_char(").append(dbField).append(", 'yy-mm-dd')");
	}

	@Override
	public void truncateToDateMinuteStr(StringBuilder builder, String dbField) {
		builder.append("to_char(").append(dbField).append(", 'yy-mm-dd hh24:mi')");
	}

	@Override
	public void truncateToDateSecondStr(StringBuilder builder, String dbField) {
		builder.append("to_char(").append(dbField).append(", 'yy-mm-dd hh24:mi:ss')");
	}

	@Override
	public PaginateSql forPaginate(String fieldSelectSql, String fromWhereSql, Integer max, Long offset) {
		PaginateSql paginateSql = new PaginateSql();
		
		if (max == null && offset == null) {
			paginateSql.setSql(fieldSelectSql + fromWhereSql);
			return paginateSql;
		}
		
		if (offset == null) {
			offset = 0L;
		}
		
		String rowAlias = "row_";
		while (fromWhereSql.contains(rowAlias)) {
			rowAlias += "_";
		}

		StringBuilder builder = new StringBuilder();
		
		if (max == null) {
			
			builder.append("select * from (");

			builder.append(fieldSelectSql).append(fromWhereSql);
			
			builder.append(") ").append(rowAlias).append(" where rownum > ?");
			
			paginateSql.addParam(offset);
		} else {
			
			String tableAlias = "table_";
			while (fromWhereSql.contains(tableAlias)) {
				tableAlias += "_";
			}
			
			String rownumAlias = "rownum_";
			while (fieldSelectSql.contains(tableAlias)) {
				rownumAlias += "_";
			}
			
			builder.append("select * from (select ").append(rowAlias).append(".*, rownum ").append(rownumAlias);
		
			builder.append(" from (").append(fieldSelectSql).append(fromWhereSql);
			
			builder.append(") ").append(rowAlias).append(" where rownum <= ?) ").append(tableAlias);
			
			builder.append(" where ").append(tableAlias).append(".").append(rownumAlias).append(" > ?");
			
			paginateSql.addParam(offset + max);
			paginateSql.addParam(offset);
		}

		paginateSql.setSql(builder.toString());
		
		return paginateSql;
	}

}
