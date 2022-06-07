package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.DbTypeResolver;
import com.ejlchina.searcher.bean.DbType;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * 默认数据库字段类型识别器
 * @since v3.8.0
 */
public class DefaultDbTypeResolver implements DbTypeResolver {

    @Override
    public DbType resolve(Field field) {
        Class<?> type = field.getType();
        if (type == boolean.class || type == Boolean.class) {
            return DbType.BOOL;
        }
        if (type == byte.class || type == Byte.class) {
            return DbType.BYTE;
        }
        if (type == short.class || type == Short.class) {
            return DbType.SHORT;
        }
        if (type == int.class || type == Integer.class) {
            return DbType.INT;
        }
        if (type == long.class || type == Long.class) {
            return DbType.LONG;
        }
        if (type == float.class || type == Float.class) {
            return DbType.FLOAT;
        }
        if (type == double.class || type == Double.class) {
            return DbType.DOUBLE;
        }
        if (type == BigDecimal.class) {
            return DbType.DECIMAL;
        }
        if (type == String.class) {
            return DbType.STRING;
        }
        if (type == java.sql.Date.class || type == LocalDate.class) {
            return DbType.DATE;
        }
        if (type == java.sql.Time.class || type == LocalTime.class) {
            return DbType.TIME;
        }
        if (type == Date.class || type == Timestamp.class || type == LocalDateTime.class) {
            return DbType.DATETIME;
        }
        return DbType.UNKNOWN;
    }

}
