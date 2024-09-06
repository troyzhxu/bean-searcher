package com.example.demo;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.snack.core.Feature;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.serialization.snack3.SnackRenderFactory;
import org.noear.wood.DbContext;
import org.noear.wood.annotation.Db;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

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

    @Bean
    public void jsonInit(@Inject SnackRenderFactory factory){
        // 枚举序列化为名字
        factory.addFeatures(Feature.EnumUsingName);  //增加特性
    }

}
