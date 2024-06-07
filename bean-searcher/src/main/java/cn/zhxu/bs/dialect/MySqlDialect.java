package cn.zhxu.bs.dialect;

import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.param.Paging;

/**
 * MySql 方言实现
 * @author Troy.Zhou
 * */
public class MySqlDialect extends SqlPagination implements Dialect {

    @Override
    public SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
        return forPaginate(SIMPLE_LIMIT, fieldSelectSql, fromWhereSql, paging);
    }

    @Override
    public boolean allowHavingAlias() {
        return true;
    }

}
