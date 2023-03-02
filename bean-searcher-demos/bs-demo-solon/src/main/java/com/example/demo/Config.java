package com.example.demo;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
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
        String[] sqlList = readAsString(sqlFilePath).split(";");
        for (String sql : sqlList) {
            db.sql(sql).execute();
        }
    }

    public static String readAsString(String path) throws IOException {
        InputStream input = Config.class.getClassLoader().getResourceAsStream(path);
        if (input == null) {
            throw new IllegalStateException("No resource found at " + path);
        }
        return readAsString(input);
    }

    public static String readAsString(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (input; output) {
            byte[] buf = new byte[10240];
            int len;
            while ((len = input.read(buf)) != -1) {
                output.write(buf, 0, len);
            }
        }
        return output.toString();
    }

}
