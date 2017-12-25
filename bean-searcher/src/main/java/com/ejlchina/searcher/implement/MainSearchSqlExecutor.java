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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

	protected Log log = LogFactory.getLog(MainSearchSqlExecutor.class);
	
	private boolean showSql = false;
	private boolean logInConsole = false;

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
		if (!searchSql.isShouldQueryList() && !searchSql.isShouldQueryTotal()) {
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
				if (showSql) {
					doLog("sql ---- " + searchSql.getListSqlString());
					doLog("params - "
							+ Arrays.toString(searchSql.getListSqlParams().toArray()));
				}
				listStatement = connection.prepareStatement(searchSql.getListSqlString());
				fillParamsTntoStatement(listStatement, searchSql.getListSqlParams());
				listResultSet = listStatement.executeQuery();
				while (listResultSet.next()) {
					result.addTmpData(resolveResult(listResultSet, searchSql.getAliasList()));
				}
			}
			if (searchSql.isShouldQueryTotal()) {
				if (showSql) {
					doLog("sql ---- " + searchSql.getCountSqlString());
					doLog("params - "
							+ Arrays.toString(searchSql.getCountSqlParams().toArray()));
				}
				countStatement = connection.prepareStatement(searchSql.getCountSqlString());
				fillParamsTntoStatement(countStatement, searchSql.getCountSqlParams());
				countResultSet = countStatement.executeQuery();
				if (countResultSet.next()) {
					result.setTotalCount((Number) countResultSet.getObject(1));
				}
			}
		} catch (SQLException e) {
			throw new SearcherException("A exception throwed when query!", e);
		} finally {
			closeConnection(connection, listStatement, countStatement, listResultSet, countResultSet);
		}
		return result;
	}

	private void fillParamsTntoStatement(PreparedStatement statement, List<Object> params) throws SQLException {
		for (int i = 0; i < params.size(); i++) {
			statement.setObject(i + 1, params.get(i));
		}
	}

	private Map<String, Object> resolveResult(ResultSet resultSet, List<String> aliasList) throws SQLException {
		Map<String, Object> result = new HashMap<>();
		for (String alias : aliasList) {
			result.put(alias, resultSet.getObject(alias));
		}
		return result;
	}

	private void closeConnection(Connection connection, PreparedStatement listStatement,
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

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	public void setLogInConsole(boolean logInConsole) {
		this.logInConsole = logInConsole;
	}

	protected void doLog(String content) {
		content = "bean-searcher - " + content;
		if (logInConsole) {
			System.out.println(content);
		} else {
			log.info(content);
		}
	}

	public void setLog(Log log) {
		this.log = log;
	}
	
}
