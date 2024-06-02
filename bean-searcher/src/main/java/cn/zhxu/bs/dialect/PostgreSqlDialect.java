package cn.zhxu.bs.dialect;


import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.param.Paging;

/**
 * PostgreSQL 方言实现
 * @author Troy.Zhou @ 2022-04-19
 * @since v3.6.0
 * */
public class PostgreSqlDialect extends SqlPagination implements Dialect {

	@Override
	public SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
		return forPaginate(OFFSET_LIMIT, fieldSelectSql, fromWhereSql, paging);
	}

	@Override
	public boolean hasILike() {
		return true;
	}

}
