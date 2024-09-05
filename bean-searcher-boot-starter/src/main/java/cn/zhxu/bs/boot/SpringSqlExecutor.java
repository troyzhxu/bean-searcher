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
        String name = beanMeta.getDataSource();
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
    protected void closeQuietly(AutoCloseable resource) {
        if (!(resource instanceof Connection)) {
            super.closeQuietly(resource);
        }
    }
}
