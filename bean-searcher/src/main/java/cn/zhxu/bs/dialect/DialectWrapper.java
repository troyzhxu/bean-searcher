package cn.zhxu.bs.dialect;

import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.param.Paging;

import java.util.Objects;

public class DialectWrapper implements DialectSensor {

    private Dialect dialect = new MySqlDialect();

    public DialectWrapper() {
    }

    public DialectWrapper(Dialect dialect) {
        this.dialect = Objects.requireNonNull(dialect);
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

    /**
     * @return 是否允许在 having 语句中使用别名
     * @since v4.3.0
     */
    public boolean allowHavingAlias() {
        return dialect.allowHavingAlias();
    }

    /**
     * 当不支持布尔值时，框架将自动把布尔参数转换为 0 / 1 的整数值
     * @return 是否允许条件参数中出现布尔值
     * @since v4.6.0
     */
    public boolean allowBoolParams() {
        return dialect.allowBoolParams();
    }

    @Override
    public void setDialect(Dialect dialect) {
        this.dialect = Objects.requireNonNull(dialect);
    }

    public Dialect getDialect() {
        return dialect;
    }

}
