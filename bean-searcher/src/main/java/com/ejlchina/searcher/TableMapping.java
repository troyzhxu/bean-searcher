package com.ejlchina.searcher;

import java.lang.reflect.Field;

/**
 * 注解缺省时的 与字段映射
 * @author Troy.Zhou @ 2021-11-01
 * @since v3.0.0
 */
public interface TableMapping {

    /**
     * 根据类获取表名
     * @param beanClass SearchBean 的 Class
     * @return 表名
     */
    String toTableName(Class<?> beanClass);

    /**
     * 根据 SearchBean 的字段获取表列名
     * @param field 字段
     * @return 表列名
     */
    String toColumnName(Field field);


}
