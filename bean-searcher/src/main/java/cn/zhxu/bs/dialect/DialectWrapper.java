package cn.zhxu.bs.dialect;

import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.param.Paging;

public class DialectWrapper implements DialectSensor {

    private Dialect dialect = new MySqlDialect();

    public DialectWrapper() {
    }

    public DialectWrapper(Dialect dialect) {
        this.dialect = dialect;
    }

    /**
     * 把字段 dbField 转换为大写
     * @param builder sql builder
     * @param dbField 数据库字段
     */
    public void toUpperCase(StringBuilder builder, String dbField) {
        dialect.toUpperCase(builder, dbField);
    }

    /**
     * @return 是否支持 ilike 语法
     * @since v3.7.0
     */
    public boolean hasILike() {
        return dialect.hasILike();
    }

    /**
     * 分页
     * @param fieldSelectSql 查询语句
     * @param fromWhereSql 条件语句
     * @param paging 分页参数
     * @return 分页Sql
     */
    public SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
        return dialect.forPaginate(fieldSelectSql, fromWhereSql, paging);
    }

    @Override
    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public Dialect getDialect() {
        return dialect;
    }

}
