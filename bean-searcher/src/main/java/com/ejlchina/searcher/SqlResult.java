package com.ejlchina.searcher;

import java.io.Closeable;
import java.sql.SQLException;

/**
 * SQL 执行结果
 */
public class SqlResult<T> implements Closeable {

    /**
     * 结果
     * @since v3.6.0
     */
    public interface Result {

        /**
         * 获取当前记录的某一列的值
         * @param columnLabel 列名（别名）
         * @return 指定列的值
         */
        Object get(String columnLabel) throws SQLException;

        /**
         * 关闭结果
         * @throws SQLException 异常
         */
        default void close() throws SQLException {}

    }

    /**
     * 结果集
     * @since v3.6.0
     */
    public interface ResultSet extends Result {

        /**
         * 游标移动到下一条记录
         * @return 下一条记录是否存在
         */
        boolean next() throws SQLException;

    }


    /**
     * 检索 SQL 信息
     */
    private final SearchSql<T> searchSql;

    /**
     * 列表查询结果集
     */
    private ResultSet listResult;

    /**
     * 聚合查询结果集
     */
    private Result clusterResult;


    public SqlResult(SearchSql<T> searchSql) {
        this.searchSql = searchSql;
    }

    /**
     * 关闭结果集
     */
    @Override
    public void close() {
        try {
            if (listResult != null) {
                listResult.close();
            }
            if (clusterResult != null) {
                clusterResult.close();
            }
        } catch (SQLException e) {
            throw new SearchException("Can not close result or resultSet!", e);
        }
    }

    public SearchSql<T> getSearchSql() {
        return searchSql;
    }

    public ResultSet getListResult() {
        return listResult;
    }

    public void setListResult(ResultSet listResult) {
        this.listResult = listResult;
    }

    public Result getClusterResult() {
        return clusterResult;
    }

    public void setClusterResult(Result clusterResult) {
        this.clusterResult = clusterResult;
    }

}
