package cn.zhxu.bs.dialect;


import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.param.Paging;

/**
 * SqlServer (v2012+) 方言实现
 * @author Troy.Zhou @ 2022-05-22
 * @since v3.7.0
 * */
public class SqlServerDialect extends SqlPagination implements Dialect {

    @Override
    public SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
        return forPaginate(OFFSET_FETCH, fieldSelectSql, fromWhereSql, paging);
    }

    @Override
    public boolean allowBoolLiterals() {
        return false;
    }

}
