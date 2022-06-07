package com.ejlchina.searcher.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * 数据库字段类型
 * @since v3.8.0
 * @author Troy.Zhou @ 2022-06-07
 */
public enum DbType {

    /**
     * 布尔字段
     */
    BOOL(Boolean.class),

    /**
     * 字节
     */
    BYTE(Byte.class),

    /**
     * 短整形
     */
    SHORT(Short.class),

    /**
     * 整形字段
     */
    INT(Integer.class),

    /**
     * 长整形字段
     */
    LONG(Long.class),

    /**
     * 浮点型
     */
    FLOAT(Float.class),

    /**
     * 双精度
     */
    DOUBLE(Double.class),

    /**
     * 金融数字
     */
    DECIMAL(BigDecimal.class),

    /**
     * 字符串
     */
    STRING(String.class),

    /**
     * 日期（没有时间）
     */
    DATE(Date.class),

    /**
     * 时间（没有日期）
     */
    TIME(Time.class),

    /**
     * 日期+时间
     */
    DATETIME(Timestamp.class),

    /**
     * 未知（自动推断）
     */
    UNKNOWN(null);


    private final Class<?> type;

    DbType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

}
