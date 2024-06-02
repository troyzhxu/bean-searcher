package cn.zhxu.bs.dialect;

import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.param.Paging;

/**
 * 分页方法
 * @author Troy.Zhou @ 2024-06-02
 * @since v4.3.0
 */
public class SqlPagination {

    public static final String OFFSET_LIMIT = " offset ? limit ?";
    public static final String OFFSET_FETCH = " offset ? rows fetch next ? rows only";

    /**
     * @param method 分页方法
     * @param fieldSelectSql 查询语句
     * @param fromWhereSql 条件语句
     * @param paging 分页参数（可空，为空时表示不分页）
     * @return 分页 Sql
     */
    protected SqlWrapper<Object> forPaginate(String method, String fieldSelectSql, String fromWhereSql, Paging paging) {
        SqlWrapper<Object> wrapper = new SqlWrapper<>();
        StringBuilder ret = new StringBuilder();
        ret.append(fieldSelectSql).append(fromWhereSql);
        if (paging != null) {
            ret.append(method);
            wrapper.addPara(paging.getOffset());
            wrapper.addPara(paging.getSize());
        }
        wrapper.setSql(ret.toString());
        return wrapper;
    }

}
