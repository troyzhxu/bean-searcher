package cn.zhxu.bs.dialect;

import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.param.Paging;

/**
 * Oracle 方言实现
 * 
 * @author Troy.Zhou
 * @since V1.1.1
 * */
public class OracleDialect implements Dialect {

	@Override
	public SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
		SqlWrapper<Object> wrapper = new SqlWrapper<>();
		if (paging == null) {
			wrapper.setSql(fieldSelectSql + fromWhereSql);
			return wrapper;
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
		wrapper.addPara(offset + size);
		wrapper.addPara(offset);
		wrapper.setSql(builder.toString());
		return wrapper;
	}

}
