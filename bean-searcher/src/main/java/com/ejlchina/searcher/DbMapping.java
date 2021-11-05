package com.ejlchina.searcher;

import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;

import java.lang.reflect.Field;

/**
 * 在 {@link SearchBean } 或 {@link DbField } 缺省时
 * 自动与数据库表名与字段名映射
 * @author Troy.Zhou @ 2021-11-01
 * @since v3.0.0
 */
public interface DbMapping {

    /**
     * 根据 beanClass 获取表名
     * 在 SearchBean 的类上没加 @SearchBean 注解时 或注解的 tables 属性为空时，根据该方法自动获取表信息
     * @param beanClass SearchBean 的 Class
     * @return 表名，若返回空，则表示 beanClass 不支持检索
     */
    String table(Class<?> beanClass);

    /**
     * 根据 field 获取表列名
     * 在 SearchBean 的某字段上没加 @DbField 注解，同时 没加 @SearchBean 注解 或 加了但 tables 属性为空 或指定了 autoMapTo 属性时，根据该方法自动获取字段信息
     * @param field 类字段
     * @return 列名，若返回空，则表示忽略该字段
     */
    String column(Field field);

}
