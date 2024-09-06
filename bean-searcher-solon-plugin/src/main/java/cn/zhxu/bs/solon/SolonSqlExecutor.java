package cn.zhxu.bs.solon;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.implement.DefaultSqlExecutor;
import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 支持 Solon 事务的 Sql 执行器
 * @author Troy.Zhou
 * @since 4.3.2
 */
public class SolonSqlExecutor extends DefaultSqlExecutor {

    public SolonSqlExecutor(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Connection getConnection(BeanMeta<?> beanMeta) throws SQLException {
        DataSource dataSource = requireDataSource(beanMeta);
        return TranUtils.getConnectionProxy(dataSource);
    }

}
