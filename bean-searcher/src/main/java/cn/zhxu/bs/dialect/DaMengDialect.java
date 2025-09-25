package cn.zhxu.bs.dialect;

import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.param.Paging;

/**
 * 达梦数据库 方言实现
 * @author Troy.Zhou
 * @since v4.6.0
 * */
public class DaMengDialect extends SqlPagination implements Dialect {

    @Override
    public SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
        return forPaginate(OFFSET_LIMIT, fieldSelectSql, fromWhereSql, paging);
    }

    @Override
    public boolean allowBoolLiterals() {
        return false;
    }

}
