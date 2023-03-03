package com.example.demo;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.ParamFilter;
import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.wood.DbContext;
import org.noear.wood.annotation.Db;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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

    private void exeSqlFile(DbContext db, String sqlFilePath) throws IOException, SQLException {
        for (String sql : ResourceUtil.getResourceAsString(sqlFilePath)
                .split(";")) {
            db.sql(sql).execute();
        }
    }

    @Bean
    public ParamFilter currentRequestParamFilter() {
        return new ParamFilter() {
            @Override
            public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
                // 获取当前请求的所有参数
                Map<String, Object> params = flat(Context.current().paramsMap());
                params.putAll(paraMap);
                return params;
            }
        };
    }

    public static Map<String, Object> flat(Map<String, List<String>> map) {
        Map<String, Object> newMap = new HashMap<>();
        for (Map.Entry<String, List<String>> entry: map.entrySet()) {
            List<String> values = entry.getValue();
            if (values.size() > 0) {
                newMap.put(entry.getKey(), values.get(0));
            }
        }
        return newMap;
    }

}
