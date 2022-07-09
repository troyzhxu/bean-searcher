package com.ejlchina.searcher;

import java.io.Closeable;
import java.sql.SQLException;

/**
 * SQL 执行结果
 */
public class SqlResult<T> implements Closeable {

    /**
     * 结果（单条记录）
     * @since v3.6.0
     */
    public interface Result extends AutoCloseable {

        /**
         * 获取当前记录的某一列的值
         * @param columnLabel 列名（别名）
         * @return 指定列的值
         * @throws SQLException 异常
         */
        Object get(String columnLabel) throws SQLException;

        /**
         * 释放资源
         * @throws SQLException 异常
         */
        default void close() throws SQLException {}

    }

    /**
     * 结果集（多条记录）
     * @since v3.6.0
     */
    public interface ResultSet extends Result {

        /**
         * 空结果集
         * @since v3.7.0
         */
        ResultSet EMPTY = new ResultSet() {
            @Override
            public boolean next() { return false; }
            @Override
            public Object get(String columnLabel) { return null; }
        };

        /**
         * 游标移动到下一条记录
         * @return 下一条记录是否存在
         * @throws SQLException 异常
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
    private final ResultSet listResult;

    /**
     * 聚合查询结果集
     */
    private final Result clusterResult;


    public SqlResult(SearchSql<T> searchSql) {
        this(searchSql, null, null);
    }

    public SqlResult(SearchSql<T> searchSql, ResultSet listResult, Result clusterResult) {
        this.searchSql = searchSql;
        this.listResult = listResult;
        this.clusterResult = clusterResult;
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

    public Result getClusterResult() {
        return clusterResult;
    }

}
