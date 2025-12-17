package cn.zhxu.bs.dialect;

import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.param.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SqlServer (v2012+) 方言实现
 * @author Troy.Zhou @ 2022-05-22
 * @since v3.7.0
 * */
public class SqlServerDialect extends SqlPagination implements Dialect {

    final static Logger log = LoggerFactory.getLogger(SqlServerDialect.class);

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
        log.warn("Your SQL statement uses pagination but does not specify a sorting field, which may cause it to fail in SQL Server!");
        return forPaginate(OFFSET_FETCH, fieldSelectSql, fromWhereSql, paging);
    }

    @Override
    public boolean allowBoolLiterals() {
        return false;
    }

}
