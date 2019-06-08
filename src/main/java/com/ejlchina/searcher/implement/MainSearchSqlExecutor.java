package com.ejlchina.searcher.implement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejlchina.searcher.SearchSql;
import com.ejlchina.searcher.SearchSqlExecutor;
import com.ejlchina.searcher.SearchTmpResult;
import com.ejlchina.searcher.SearcherException;

/**
 * JDBC Search Sql 执行器
 * 
 * @author Troy.Zhou
 * @since 1.1.1
 * 
 */
public class MainSearchSqlExecutor implements SearchSqlExecutor {


	protected Logger log = LoggerFactory.getLogger(MainSearchSqlExecutor.class);
	
	
	private DataSource dataSource;

	
	public MainSearchSqlExecutor() {
		super();
	}

	
	public MainSearchSqlExecutor(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 设置数据源
	 * 
	 * @param dataSource
	 *            数据源
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public SearchTmpResult execute(SearchSql searchSql) {
		SearchTmpResult result = new SearchTmpResult();
		if (!searchSql.isShouldQueryList() && !searchSql.isShouldQueryCluster()) {
			return result;
		}
		if (dataSource == null) {
			throw new SearcherException("You must config a dataSource for MainSearchSqlExecutor!");
		}
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			throw new SearcherException("Can not get Connection from dataSource!", e);
		}
		PreparedStatement listStatement = null;
		PreparedStatement countStatement = null;
		ResultSet listResultSet = null;
		ResultSet countResultSet = null;
		try {
			if (searchSql.isShouldQueryList()) {
				doLog("sql ---- " + searchSql.getListSqlString());
				doLog("params - " + Arrays.toString(searchSql.getListSqlParams().toArray()));
				executeListSqlAndCollectResult(connection, 
						searchSql.getListSqlString(), 
						searchSql.getListSqlParams(), 
						searchSql.getListAliases(), 
						result);
			}
			if (searchSql.isShouldQueryCluster()) {
				doLog("sql ---- " + searchSql.getClusterSqlString());
				doLog("params - " + Arrays.toString(searchSql.getClusterSqlParams().toArray()));
				executeClusterSqlAndCollectResult(connection, 
						searchSql.getClusterSqlString(), 
						searchSql.getClusterSqlParams(), 
						searchSql.getCountAlias(), 
						searchSql.getSummaryAliases(), 
						result);
			}
		} catch (SQLException e) {
			throw new SearcherException("A exception throwed when query!", e);
		} finally {
			closeConnection(connection, listStatement, countStatement, listResultSet, countResultSet);
		}
		return result;
	}

	
	protected void executeListSqlAndCollectResult(Connection connection, String sql, List<Object> params, 
			List<String> listAliases, SearchTmpResult result) throws SQLException {
		PreparedStatement listStatement = connection.prepareStatement(sql);
		putParamsTntoStatement(listStatement, params);
		ResultSet listResultSet = listStatement.executeQuery();
		while (listResultSet.next()) {
			result.addTmpData(resolveDataResult(listResultSet, listAliases));
		}
	}
	
	protected void executeClusterSqlAndCollectResult(Connection connection, String sqlString, List<Object> sqlParams, 
			String countAlias, List<String> summaryAliases, SearchTmpResult result) throws SQLException {
		PreparedStatement countStatement = connection.prepareStatement(sqlString);
		putParamsTntoStatement(countStatement, sqlParams);
		ResultSet countResultSet = countStatement.executeQuery();
		if (countResultSet.next()) {
			collectClusterResult(countAlias, summaryAliases, result, countResultSet);
		}
	}
	
	protected void collectClusterResult(String countAlias, List<String> summaryAliases, 
			SearchTmpResult result, ResultSet countResultSet)
			throws SQLException {
		if (countAlias != null) {
			result.setTotalCount((Number) countResultSet.getObject(countAlias));
		}
		Number[] summaries = new Number[summaryAliases.size()];
		for (int i = 0; i < summaries.length; i++) {
			String summaryAlias = summaryAliases.get(i);
			summaries[i] = (Number) countResultSet.getObject(summaryAlias);
		}
		result.setSummaries(summaries);
	}


	protected void putParamsTntoStatement(PreparedStatement statement, List<Object> params) throws SQLException {
		for (int i = 0; i < params.size(); i++) {
			statement.setObject(i + 1, params.get(i));
		}
	}

	protected Map<String, Object> resolveDataResult(ResultSet resultSet, List<String> aliasList) throws SQLException {
		Map<String, Object> result = new HashMap<>();
		for (String alias : aliasList) {
			result.put(alias, resultSet.getObject(alias));
		}
		return result;
	}

	protected void closeConnection(Connection connection, PreparedStatement listStatement,
			PreparedStatement countStatement, ResultSet listResultSet, ResultSet countResultSet) {
		try {
			if (countStatement != null) {
				countStatement.close();
			}
			if (listStatement != null) {
				listStatement.close();
			}
			if (connection != null) {
				connection.close();
			}
			if (listResultSet != null) {
				listResultSet.close();
			}
			if (countResultSet != null) {
				countResultSet.close();
			}
		} catch (SQLException e) {
			throw new SearcherException("Can not close connection!", e);
		}
	}

	protected void doLog(String content) {
		log.info("bean-searcher - " + content);
	}
	
}
