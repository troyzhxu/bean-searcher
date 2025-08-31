package com.example.config;

import cn.zhxu.bs.SqlExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SlowSqlListener implements SqlExecutor.SlowListener {

    @Override
    public void onSlowSql(Class<?> beanClass, String slowSql, List<Object> params, long timeCost) {
        System.out.println("慢 SQL 啦");
    }

}
