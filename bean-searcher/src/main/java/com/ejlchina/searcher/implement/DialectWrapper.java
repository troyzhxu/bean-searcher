package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.dialect.Dialect;
import com.ejlchina.searcher.dialect.DialectSensor;
import com.ejlchina.searcher.dialect.MySqlDialect;
import com.ejlchina.searcher.param.Paging;

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
     * 分页
     * @param fieldSelectSql 查询语句
     * @param fromWhereSql 条件语句
     * @param paging 分页参数
     * @return 分页Sql
     */
    public Dialect.PaginateSql forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
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
