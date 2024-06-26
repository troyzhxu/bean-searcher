package com.example.config;

import cn.zhxu.bs.*;
import cn.zhxu.bs.boot.DataSourceDialect;
import cn.zhxu.bs.boot.NamedDataSource;
import cn.zhxu.bs.dialect.MySqlDialect;
import cn.zhxu.bs.param.Paging;
import com.example.operator.MyOp;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class BeanSearcherConfig {

    // 自定义运算符
    @Bean
    public MyOp myOp() {
        return new MyOp();
    }

    /**
     * @return 慢 SQL 监听器
     */
    @Bean
    public SqlExecutor.SlowListener slowSqlListener() {
        return (beanClass, slowSql, params, timeCost) -> {
            System.out.println("慢 SQL 啦");
        };
    }

    @Order(1)
    @Bean
    public ParamFilter paramFilter1() {
        return new ParamFilter() {
            @Override
            public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) throws IllegalParamException {
                System.out.println("前一个过滤器");
                return paraMap;
            }
        };
    }

    @Order(2)
    @Bean
    public ParamFilter paramFilter2() {
        return new ParamFilter() {
            @Override
            public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) throws IllegalParamException {
                System.out.println("后一个过滤器");
                return paraMap;
            }
        };
    }

//    /**
//     * 为了简化多值参数传递
//     * 参考：https://github.com/troyzhxu/bean-searcher/issues/10
//     * @return 参数过滤器
//     */
//    @Bean
//    public ParamFilter myParamFilter() {
//        return new JsonArrParamFilter();
//    }

    /**
     * 以下是多数据源配置示例，可全部删除
     */

    @Bean(name = "primaryDsProps")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties primaryDsProps() {
        return new DataSourceProperties();
    }

    @Bean(name = "userDsProps")
    @ConfigurationProperties(prefix = "spring.datasource.user")
    public DataSourceProperties userDsProps() {
        return new DataSourceProperties();
    }

    @Bean(name = "orderDsProps")
    @ConfigurationProperties(prefix = "spring.datasource.order")
    public DataSourceProperties orderDsProps() {
        return new DataSourceProperties();
    }

    @Bean @Primary
    public DataSource primaryDs(@Qualifier("primaryDsProps") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "userDs")
    public DataSource userDs(@Qualifier("userDsProps") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "orderDs")
    public DataSource orderDs(@Qualifier("orderDsProps") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    public NamedDataSource userDataSource(@Qualifier("userDs") DataSource userDs) {
        return new NamedDataSource("userDs", userDs);
    }

    @Bean
    public NamedDataSource orderDataSource(@Qualifier("orderDs") DataSource orderDs) {
        return new NamedDataSource("orderDs", orderDs);
    }

    @Bean
    public DataSourceDialect dataSourceDialect1() {
        return new DataSourceDialect("userDs", new MySqlDialect() {
            @Override
            public SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
                System.out.println("使用 USER 方言 forPaginate");
                return super.forPaginate(fieldSelectSql, fromWhereSql, paging);
            }
            @Override
            public void toUpperCase(StringBuilder builder, String dbField) {
                System.out.println("使用 USER 方言 toUpperCase");
                super.toUpperCase(builder, dbField);
            }
            @Override
            public boolean hasILike() {
                System.out.println("使用 USER 方言 hasILike");
                return super.hasILike();
            }
        });
    }

    @Bean
    public DataSourceDialect dataSourceDialect2() {
        return new DataSourceDialect("orderDs", new MySqlDialect() {
            @Override
            public SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
                System.out.println("使用 ORDER 方言 forPaginate");
                return super.forPaginate(fieldSelectSql, fromWhereSql, paging);
            }
            @Override
            public void toUpperCase(StringBuilder builder, String dbField) {
                System.out.println("使用 ORDER 方言 toUpperCase");
                super.toUpperCase(builder, dbField);
            }
            @Override
            public boolean hasILike() {
                System.out.println("使用 ORDER 方言 hasILike");
                return super.hasILike();
            }
        });
    }

}
