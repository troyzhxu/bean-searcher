package com.example.demo;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.ParamFilter;
import com.zaxxer.hikari.HikariDataSource;
import org.noear.snack.core.Feature;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.serialization.snack3.SnackRenderFactory;
import org.noear.wood.DbContext;
import org.noear.wood.annotation.Db;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class Config {

    @Bean
    public DataSource ds(@Inject("${db}") HikariDataSource ds) {
        return ds;
    }

    @Bean
    public void sqlInit(@Db DbContext db) throws IOException, SQLException {
        // 初始化表
        exeSqlFile(db, "db/schema.sql");
        // 初始化数据
        exeSqlFile(db, "db/data.sql");
    }

    private void exeSqlFile(DbContext db, String filePath) throws IOException, SQLException {
        for (String sql : ResourceUtil.getResourceAsString(filePath)
                .split(";")) {
            db.sql(sql).execute();
        }
    }

    @Bean(name = "currentRequestParamFilter")
    public ParamFilter currentRequestParamFilter() {
        return new ParamFilter() {
            @Override
            public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
                System.out.println("currentRequestParamFilter");
                // 获取当前请求的所有参数
                Map<String, Object> params = new HashMap<>(Context.current().paramMap());
                // 用户传入的参数优先级更高
                params.putAll(paraMap);
                return params;
            }
        };
    }

    @Bean(name = "paramFilter1", index = 2)
    public ParamFilter paramFilter1() {
        return new ParamFilter() {
            @Override
            public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
                System.out.println("前一个过滤器");
                return paraMap;
            }
        };
    }

    @Bean(name = "paramFilter2", index = 1)
    public ParamFilter paramFilter2() {
        return new ParamFilter() {
            @Override
            public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
                System.out.println("后一个过滤器");
                return paraMap;
            }
        };
    }

    @Bean
    public void jsonInit(@Inject SnackRenderFactory factory){
        // 枚举序列化为名字
        factory.addFeatures(Feature.EnumUsingName);  //增加特性
    }

}
