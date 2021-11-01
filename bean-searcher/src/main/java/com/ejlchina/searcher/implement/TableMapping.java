package com.ejlchina.searcher.implement;

import java.lang.reflect.Field;

/**
 * 注解缺省时的表名与字段映射
 * @author Troy.Zhou @ 2021-11-01
 * @since v3.0.0
 */
public class TableMapping {

    /**
     * 根据类获取表名
     * @param beanClass SearchBean 的 Class
     * @return 表名
     */
    public String tableName(Class<?> beanClass) {
        String className = beanClass.getSimpleName();
        // TODO: 表名映射
        return className;
    }

    /**
     * 根据 SearchBean 的字段获取表列名
     * @param field 字段
     * @return 表列名
     */
    public String columnName(Field field) {
        String fieldName = field.getName();
        // TODO: 字段映射
        return fieldName;
    }


}
