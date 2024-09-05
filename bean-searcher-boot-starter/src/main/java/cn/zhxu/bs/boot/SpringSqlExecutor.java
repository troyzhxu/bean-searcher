package cn.zhxu.bs.boot;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.SearchException;
import cn.zhxu.bs.implement.DefaultSqlExecutor;
import cn.zhxu.bs.util.StringUtils;
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
        final String name = beanMeta.getDataSource();
        if (StringUtils.isBlank(name)) {
            if (dataSource == null) {
                throw new SearchException("There is not a default dataSource for " + beanMeta.getBeanClass());
            }
            return DataSourceUtils.doGetConnection(dataSource);
        }
        DataSource dataSource = this.getDataSourceMap().get(name);
        if (dataSource == null) {
            throw new SearchException("There is not a dataSource named " + name + " for " + beanMeta.getBeanClass());
        }
        return DataSourceUtils.doGetConnection(dataSource);
    }

    @Override
    protected <T> void closeQuietly(AutoCloseable resource, BeanMeta<T> beanMeta) {
        if (resource instanceof Connection) {
            final String name = beanMeta.getDataSource();
            DataSource thisDataSource = dataSource;
            if (StringUtils.isNotBlank(name)) {
                thisDataSource = this.getDataSourceMap().get(name);
            }
            DataSourceUtils.releaseConnection((Connection) resource, thisDataSource);
        } else {
            super.closeQuietly(resource, beanMeta);
        }
    }
}
