package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.bean.SearchBean;
import com.ejlchina.searcher.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JDBC Sql 执行器
 * 
 * @author Troy.Zhou
 * @since 1.1.1
 * 
 */
public class DefaultSqlExecutor implements SqlExecutor {


	protected Logger log = LoggerFactory.getLogger(DefaultSqlExecutor.class);

	/**
	 * 默认数据源
	 */
	private DataSource dataSource;

	/**
	 * 具名数据源
	 * @since v3.0.0
	 */
	private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

	/**
	 * 是否使用只读事务
	 */
	private boolean transactional = false;

	/**
	 * 使用事务时的隔离级别，默认为 READ_COMMITTED
	 * @since v3.1.0
	 */
	private int transactionIsolation = Connection.TRANSACTION_READ_COMMITTED;


	public DefaultSqlExecutor() {
	}
	
	public DefaultSqlExecutor(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	
	@Override
	public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
		if (!searchSql.isShouldQueryList() && !searchSql.isShouldQueryCluster()) {
			return new SqlResult<>(searchSql);
		}
		Connection connection;
		try {
			connection = getConnection(searchSql.getBeanMeta());
		} catch (SQLException e) {
			throw new SearchException("Can not get connection from dataSource!", e);
		}
		try {
			return doExecute(searchSql, connection);
		} catch (SQLException e) {
			// 如果有异常，则立马关闭，否则与 SqlResult 一起关闭
			closeConnection(connection);
			throw new SearchException("A exception occurred when query!", e);
		}
	}

	protected Connection getConnection(BeanMeta<?> beanMeta) throws SQLException {
		String name = beanMeta.getDataSource();
		if (StringUtils.isBlank(name)) {
			if (dataSource == null) {
				throw new SearchException("There is no default dataSource for " + beanMeta.getBeanClass());
			}
			return dataSource.getConnection();
		}
		DataSource dataSource = dataSourceMap.get(name);
		if (dataSource == null) {
			throw new SearchException("There is no dataSource named " + name + " for " + beanMeta.getBeanClass());
		}
		return dataSource.getConnection();
	}

	protected <T> SqlResult<T> doExecute(SearchSql<T> searchSql, Connection connection) throws SQLException {
		if (transactional) {
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(transactionIsolation);
			connection.setReadOnly(true);
		}
		SqlResult<T> result = new SqlResult<T>(searchSql) {
			@Override
			public void close() {
				try {
					super.close();
				} finally {
					closeConnection(connection);
				}
			}
		};
		try {
			if (searchSql.isShouldQueryList()) {
				String sql = searchSql.getListSqlString();
				List<Object> params = searchSql.getListSqlParams();
				writeLog(sql, params);
				executeListSqlAndCollectResult(connection, sql, params, result);
			}
			if (searchSql.isShouldQueryCluster()) {
				String sql = searchSql.getClusterSqlString();
				List<Object> params = searchSql.getClusterSqlParams();
				writeLog(sql, params);
				executeClusterSqlAndCollectResult(connection, sql, params, result);
			}
		} finally {
			if (transactional) {
				connection.commit();
				connection.setReadOnly(false);
			}
		}
		return result;
	}

	protected void writeLog(String sql, List<Object> params) {
		log.debug("bean-searcher - sql ---- {}", sql);
		log.debug("bean-searcher - params - {}", params);
	}

	protected void executeListSqlAndCollectResult(Connection connection, String sql, List<Object> params,
				SqlResult<?> sqlResult) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(sql);
		setStatementParams(statement, params);
		ResultSet resultSet = statement.executeQuery();
		sqlResult.setListResult(new SqlResult.ResultSet() {

			@Override
			public boolean next() throws SQLException {
				return resultSet.next();
			}

			@Override
			public Object get(String columnLabel) throws SQLException {
				return resultSet.getObject(columnLabel);
			}

			@Override
			public void close() throws SQLException {
				try {
					resultSet.close();
				} finally {
					statement.close();
				}
			}

		});
	}

	protected void executeClusterSqlAndCollectResult(Connection connection, String sqlString, List<Object> sqlParams,
				SqlResult<?> sqlResult) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(sqlString);
		setStatementParams(statement, sqlParams);
		ResultSet resultSet = statement.executeQuery();
		boolean hasValue = resultSet.next();
		sqlResult.setClusterResult(new SqlResult.Result() {

			@Override
			public Object get(String columnLabel) throws SQLException {
				if (hasValue) {
					return resultSet.getObject(columnLabel);
				}
				return null;
			}

			@Override
			public void close() throws SQLException {
				try {
					resultSet.close();
				} finally {
					statement.close();
				}
			}

		});
	}

	protected void setStatementParams(PreparedStatement statement, List<Object> params) throws SQLException {
		for (int i = 0; i < params.size(); i++) {
			statement.setObject(i + 1, params.get(i));
		}
	}

	protected void closeConnection(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			throw new SearchException("Can not close connection!", e);
		}
	}

	/**
	 * 设置默认数据源
	 * @param dataSource 数据源
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = Objects.requireNonNull(dataSource);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * 添加具名数据源
	 * @see SearchBean#dataSource()
	 * @param name 数据源名称
	 * @param dataSource 数据源
	 * @since v3.1.0
	 */
	public void setDataSource(String name, DataSource dataSource) {
		if (name != null && dataSource != null) {
			dataSourceMap.put(name.trim(), dataSource);
		}
	}

	/**
	 * 添加数据源
	 * Deprecated from v3.1.0
	 * 请使用 {@link #setDataSource(String scope, DataSource dataSource) } 方法
	 * @since v3.0.0
	 * @param name 数据源名称
	 * @param dataSource 数据源
	 */
	@Deprecated
	public void addDataSource(String name, DataSource dataSource) {
		setDataSource(name, dataSource);
	}

	public Map<String, DataSource> getDataSourceMap() {
		return dataSourceMap;
	}

	/**
	 * 设置是否使用只读事务
	 * @param transactional 是否使用事务
	 */
	public void setTransactional(boolean transactional) {
		this.transactional = transactional;
	}

	public boolean isTransactional() {
		return transactional;
	}

	public int getTransactionIsolation() {
		return transactionIsolation;
	}

	/**
	 * 设置只读事务的隔离级别（只在开启了事务后有效）
	 * @param level 隔离级别
	 * @see Connection#TRANSACTION_NONE
	 * @see Connection#TRANSACTION_READ_UNCOMMITTED
	 * @see Connection#TRANSACTION_READ_COMMITTED
	 * @see Connection#TRANSACTION_REPEATABLE_READ
	 * @see Connection#TRANSACTION_SERIALIZABLE
	 */
	public void setTransactionIsolation(int level) {
		this.transactionIsolation = level;
	}

}
