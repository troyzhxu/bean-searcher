package com.ejlchina.searcher;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    // clusterResult 是否已未执行过 next 方法
    private boolean clusterNotReady = true;

    /**
     * 列表查询语句
     */
    private Statement listStatement;

    /**
     * 聚合查询语句
     */
    private Statement clusterStatement;


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
            if (listStatement != null) {
                listStatement.close();
            }
            if (clusterStatement != null) {
                clusterStatement.close();
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

    public void setListResult(ResultSet listResult, Statement listStatement) {
        this.listResult = listResult;
        this.listStatement = listStatement;
    }

    public ResultSet getAlreadyClusterResult() throws SQLException {
        if (clusterResult != null) {
            // 为了兼容 ShardingSphere，这里不能使用 ResultSet#isBeforeFirst() 方法，因为 ShardingSphere 没有实现它
            if (clusterNotReady) {
                clusterResult.next();
                clusterNotReady = false;
            }
        }
        return clusterResult;
    }

    public void setClusterResult(ResultSet clusterResult, Statement clusterStatement) {
        this.clusterResult = clusterResult;
        this.clusterStatement = clusterStatement;
    }

}
