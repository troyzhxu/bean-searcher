package cn.zhxu.bs.support;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.ParamFilter;
import cn.zhxu.bs.SearchSql;
import cn.zhxu.bs.SqlInterceptor;
import cn.zhxu.bs.dialect.DynamicDialect;
import cn.zhxu.bs.param.FetchType;

import java.util.Map;

/**
 * 动态方言处理器
 * @author Troy.Zhou
 * @since v4.1.0
 */
public class DynamicDialectSupport implements ParamFilter, SqlInterceptor {

    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
        // 动态方言必然是与多数据源配合使用的
        // 这里的实现适用于静态多数据源的场景：https://bs.zhxu.cn/guide/latest/advance.html#%E9%9D%99%E6%80%81%E6%95%B0%E6%8D%AE%E6%BA%90
        // 如果您使用的是动态数据源，那么可以重写该方法
        String dataSource = beanMeta.getDataSource();
        if (dataSource != null) {
            DynamicDialect.setCurrent(dataSource);
        }
        return paraMap;
    }

    @Override
    public <T> SearchSql<T> intercept(SearchSql<T> searchSql, Map<String, Object> paraMap, FetchType fetchType) {
        // 当代码走到 SqlInterceptor 里时，说明方言已经使用完毕，此时将之置空
        DynamicDialect.setCurrent(null);
        return searchSql;
    }

}
