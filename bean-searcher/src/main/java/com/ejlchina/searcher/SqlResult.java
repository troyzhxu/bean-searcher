package com.ejlchina.searcher;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQL 执行结果
 */
public class SqlResult<T> implements Closeable {

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
    private ResultSet clusterResult;


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
            throw new SearchException("Can not close statement or resultSet!", e);
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

    public ResultSet getClusterResult() {
        return clusterResult;
    }

    public void setClusterResult(ResultSet clusterResult) {
        this.clusterResult = clusterResult;
    }

}
