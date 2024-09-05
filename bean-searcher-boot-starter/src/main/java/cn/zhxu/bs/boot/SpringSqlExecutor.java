package cn.zhxu.bs.boot;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.implement.DefaultSqlExecutor;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 支持 Spring 事务的 Sql 执行器
 *
 * @author zengyufei
 * @since 4.3.2
 */
public class SpringSqlExecutor extends DefaultSqlExecutor {

    public SpringSqlExecutor(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Connection getConnection(BeanMeta<?> beanMeta) throws SQLException {
        DataSource dataSource = requireDataSource(beanMeta);
        return DataSourceUtils.doGetConnection(dataSource);
    }

    @Override
    protected void closeConnection(Connection connection, BeanMeta<?> beanMeta) {
        DataSource dataSource = requireDataSource(beanMeta);
        DataSourceUtils.releaseConnection(connection, dataSource);
    }

}
