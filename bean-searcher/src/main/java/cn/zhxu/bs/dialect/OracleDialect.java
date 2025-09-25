package cn.zhxu.bs.dialect;

import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.param.Paging;

/**
 * Oracle 12c+ (2013年6月发布) 方言实现
 * @author Troy.Zhou
 * */
public class OracleDialect extends SqlPagination implements Dialect {

    @Override
    public SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
        return forPaginate(OFFSET_FETCH, fieldSelectSql, fromWhereSql, paging);
    }

    @Override
    public boolean allowBoolLiterals() {
        return false;
    }

}
