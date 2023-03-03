package com.example.demo;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.wood.DbContext;
import org.noear.wood.annotation.Db;

import javax.sql.DataSource;
import java.io.*;
import java.sql.SQLException;

@Configuration
public class Config {

    @Bean
    public DataSource ds(@Inject("${datasource}") HikariDataSource ds) {
        return ds;
    }

    @Bean
    public void sqlInit(@Db DbContext db) throws IOException, SQLException {
        // 初始化表
        executeSqlFile(db, "db/schema.sql");
        // 初始化数据
        executeSqlFile(db, "db/data.sql");

        System.out.println("Department total: " + db.table("department").selectCount());
        System.out.println("Employee total: " + db.table("employee").selectCount());
    }

    private void executeSqlFile(DbContext db, String sqlFilePath) throws IOException, SQLException {
        String[] sqlList = ResourceUtil.getResourceAsString(sqlFilePath).split(";");
        for (String sql : sqlList) {
            db.sql(sql).execute();
        }
    }
}
