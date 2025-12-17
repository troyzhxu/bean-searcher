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
        if (paging == null || paging.isOrdering()) {
            return forPaginate(OFFSET_FETCH, fieldSelectSql, fromWhereSql, paging);
        }
        if (paging.getOffset() == 0) {
            // 如果只取第一页，优先使用 SELECT TOP 语法，这样可以兼容无排序字段的情况
            // since v4.8.3, 参考：https://github.com/troyzhxu/bean-searcher/issues/120
            return new SqlWrapper<>("select top " +
                    paging.getSize() +
                    fieldSelectSql.substring(6) +
                    fromWhereSql);
        }
        // 分页达到第二页，但没有指定排序字段，自动追加一个占位符排序，使其符合 SqlServer 的 SQL 语法
        return forPaginate(" order by null" + OFFSET_FETCH, fieldSelectSql, fromWhereSql, paging);
    }

    @Override
    public boolean allowBoolLiterals() {
        return false;
    }

}
