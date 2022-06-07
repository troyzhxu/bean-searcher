package com.ejlchina.searcher;

import com.ejlchina.searcher.bean.DbType;

import java.lang.reflect.Field;

/**
 * 数据库字段类型识别器
 * @since v3.8.0
 */
public interface DbTypeResolver {

    /**
     * @param field 实体类字段
     * @return 对应的数据库字段类型（非空）
     */
    DbType resolve(Field field);

}
