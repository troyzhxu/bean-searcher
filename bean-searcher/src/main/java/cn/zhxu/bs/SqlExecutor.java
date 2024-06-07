package cn.zhxu.bs;

import java.util.List;

/**
 * SQL 执行器
 * @author Troy.Zhou @ 2017-03-20
 * @since v1.0.0
 * */
public interface SqlExecutor {

    /**
     * @param <T> 泛型
     * @param searchSql 检索 SQL
     * @return 执行结果
     */
    <T> SqlResult<T> execute(SearchSql<T> searchSql);

    /**
     * 慢 SQL 监听器
     * @since v3.7.0
     */
    interface SlowListener {

        /**
         * 监听慢 SQL
         * @param beanClass 发生慢 SQL 的实体类
         * @param slowSql 慢 SQL 字符串
         * @param params SQL 的执行参数
         * @param timeCost SQL 执行时间（单位：毫秒）
         */
        void onSlowSql(Class<?> beanClass, String slowSql, List<Object> params, long timeCost);

    }

}
