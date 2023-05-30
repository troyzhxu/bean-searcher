package cn.zhxu.bs.implement;

import cn.zhxu.bs.*;
import cn.zhxu.bs.bean.SearchBean;
import cn.zhxu.bs.util.StringUtils;
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

	protected static final Logger log = LoggerFactory.getLogger(DefaultSqlExecutor.class);

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

	/**
	 * 慢 SQL 阈值（单位：毫秒），默认：500 毫秒
	 * @since v3.7.0
	 */
	private long slowSqlThreshold = 500;

	/**
	 * 慢 SQL 监听器
	 * @since v3.7.0
	 */
	private SlowListener slowListener;


	public DefaultSqlExecutor() { }
	
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
			closeQuietly(connection);
			throw new SearchException("A exception occurred when executing sql.", e);
		}
	}

	protected Connection getConnection(BeanMeta<?> beanMeta) throws SQLException {
		String name = beanMeta.getDataSource();
		if (StringUtils.isBlank(name)) {
			if (dataSource == null) {
				throw new SearchException("There is not a default dataSource for " + beanMeta.getBeanClass());
			}
			return dataSource.getConnection();
		}
		DataSource dataSource = dataSourceMap.get(name);
		if (dataSource == null) {
			throw new SearchException("There is not a dataSource named " + name + " for " + beanMeta.getBeanClass());
		}
		return dataSource.getConnection();
	}

	protected <T> SqlResult<T> doExecute(SearchSql<T> searchSql, Connection connection) throws SQLException {
		if (transactional) {
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(transactionIsolation);
			connection.setReadOnly(true);
		}
		SqlResult.ResultSet listResult = null;
		SqlResult.Result clusterResult = null;
		try {
			Number totalCount = null;
			if (searchSql.isShouldQueryCluster()) {
				clusterResult = executeClusterSql(searchSql, connection);
				String countAlias = searchSql.getCountAlias();
				if (countAlias != null) {
					totalCount = (Number) clusterResult.get(countAlias);
				}
			}
			if (searchSql.isShouldQueryList()) {
				if (totalCount == null || totalCount.longValue() > 0) {
					listResult = executeListSql(searchSql, connection);
				} else {
					listResult = SqlResult.ResultSet.EMPTY;
				}
			}
		} catch (SQLException e) {
			closeQuietly(clusterResult);
			throw e;
		} finally {
			if (transactional) {
				connection.commit();
				connection.setReadOnly(false);
			}
		}
		return new SqlResult<T>(searchSql, listResult, clusterResult) {
			@Override
			public void close() {
				try {
					super.close();
				} finally {
					closeQuietly(connection);
				}
			}
		};
	}

	protected SqlResult.ResultSet executeListSql(SearchSql<?> searchSql, Connection connection) throws SQLException {
		Result result = executeQuery(connection, searchSql.getListSqlString(),
				searchSql.getListSqlParams(), searchSql.getBeanMeta());
		ResultSet resultSet = result.resultSet;
		return new SqlResult.ResultSet() {
			@Override
			public boolean next() throws SQLException {
				return resultSet.next();
			}
			@Override
			public Object get(String columnLabel) throws SQLException {
				return resultSet.getObject(columnLabel);
			}
			@Override
			public void close() {
				result.close();
			}
		};
	}

	protected SqlResult.Result executeClusterSql(SearchSql<?> searchSql, Connection connection) throws SQLException {
		Result result = executeQuery(connection, searchSql.getClusterSqlString(),
				searchSql.getClusterSqlParams(), searchSql.getBeanMeta());
		ResultSet resultSet = result.resultSet;
		boolean hasValue;
		try {
			hasValue = resultSet.next();
		} catch (SQLException e) {
			result.close();
			throw e;
		}
		return new SqlResult.Result() {
			@Override
			public Object get(String columnLabel) throws SQLException {
				if (hasValue) {
					return resultSet.getObject(columnLabel);
				}
				return null;
			}
			@Override
			public void close() {
				result.close();
			}
		};
	}

	protected static class Result {

		final PreparedStatement statement;
		final ResultSet resultSet;

		public Result(PreparedStatement statement, ResultSet resultSet) {
			this.statement = statement;
			this.resultSet = resultSet;
		}

		public void close() {
			closeQuietly(resultSet);
			closeQuietly(statement);
		}

	}

	protected Result executeQuery(Connection connection, String sql, List<Object> params,
								  BeanMeta<?> beanMeta) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(sql);
		long t0 = System.currentTimeMillis();
		try {
			int size = params.size();
			for (int i = 0; i < size; i++) {
				statement.setObject(i + 1, params.get(i));
			}
			int timeout = beanMeta.getTimeout();
			if (timeout > 0) {
				// 这个方法比较耗时，只在 timeout 大于 0 的情况下才调用它
				statement.setQueryTimeout(timeout);
			}
			ResultSet resultSet = statement.executeQuery();
			return new Result(statement, resultSet);
		} catch (SQLException e) {
			closeQuietly(statement);
			throw e;
		} finally {
			long cost = System.currentTimeMillis() - t0;
			afterExecute(beanMeta, sql, params, cost);
		}
	}

	protected void afterExecute(BeanMeta<?> beanMeta, String sql, List<Object> params, long timeCost) {
		if (timeCost >= slowSqlThreshold) {
			Class<?> beanClass = beanMeta.getBeanClass();
			SlowListener listener = slowListener;
			if (listener != null) {
				listener.onSlowSql(beanClass, sql, params, timeCost);
			}
			log.warn("bean-searcher [{}ms] slow-sql: [{}] params: {} on [{}]", timeCost, sql, params, beanClass.getName());
		} else {
			log.debug("bean-searcher [{}ms] sql: [{}] params: {}", timeCost, sql, params);
		}
	}

	protected static void closeQuietly(AutoCloseable resource) {
		try {
			if (resource != null) {
				resource.close();
			}
		} catch (Exception e) {
			log.error("Can not close {}", resource.getClass().getSimpleName(), e);
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
	 * 设置具名数据源
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

	public long getSlowSqlThreshold() {
		return slowSqlThreshold;
	}

	/**
	 * 设置慢 SQL 阈值（最小慢 SQL 执行时间）
	 * @param slowSqlThreshold 慢 SQL 阈值，单位：毫秒
	 * @since v3.7.0
	 */
	public void setSlowSqlThreshold(long slowSqlThreshold) {
		this.slowSqlThreshold = slowSqlThreshold;
	}

	public SlowListener getSlowListener() {
		return slowListener;
	}

	public void setSlowListener(SlowListener slowListener) {
		this.slowListener = slowListener;
	}

}
