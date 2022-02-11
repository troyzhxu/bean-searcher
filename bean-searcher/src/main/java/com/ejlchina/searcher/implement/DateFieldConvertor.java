package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;

import java.sql.Timestamp;
import java.time.*;
import java.util.Date;

/**
 * 日期字段转换器
 *
 * @author Troy.Zhou @ 2021-11-12
 * @since v3.1.0
 */
public class DateFieldConvertor implements FieldConvertor.BFieldConvertor {

    /**
     * 时区
     */
    private ZoneId zoneId = ZoneId.systemDefault();

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType, Class<?> targetType) {
        return (
                valueType == Date.class ||
                valueType == java.sql.Date.class ||
                valueType == Timestamp.class ||
                valueType == LocalDateTime.class ||
                valueType == LocalDate.class)
        && (
                targetType == Date.class ||
                targetType == java.sql.Date.class ||
                targetType == Timestamp.class ||
                targetType == LocalDateTime.class ||
                targetType == LocalDate.class
        );
    }

    @Override
    public Object convert(FieldMeta meta, Object value, Class<?> targetType) {
        Class<?> valueType = value.getClass();
        if (Date.class.isAssignableFrom(valueType)) {
            Date date = (Date) value;
            if (targetType == java.sql.Date.class) {
                return new java.sql.Date(date.getTime());
            }
            if (targetType == Timestamp.class) {
                return new Timestamp(date.getTime());
            }
            if (targetType == LocalDateTime.class) {
                // 注意：java.sql.Date 的 toInstant() 方法会抛异常
                if (date instanceof java.sql.Date) {
                    LocalDate localDate = ((java.sql.Date) date).toLocalDate();
                    return LocalDateTime.of(localDate, LocalTime.of(0, 0, 0, 0));
                }
                return LocalDateTime.ofInstant(date.toInstant(), zoneId);
            }
            if (targetType == LocalDate.class) {
                // 注意：java.sql.Date 的 toInstant() 方法会抛异常
                if (date instanceof java.sql.Date) {
                    return ((java.sql.Date) date).toLocalDate();
                }
                return LocalDate.ofInstant(date.toInstant(), zoneId);
            }
            if (targetType == Date.class) {
                return date;
            }
        }
        LocalDateTime dateTime;
        if (valueType == LocalDateTime.class) {
            dateTime = (LocalDateTime) value;
        } else {
            dateTime = LocalDateTime.of((LocalDate) value, LocalTime.of(0, 0));
        }
        if (targetType == LocalDateTime.class) {
            return dateTime;
        }
        Instant instant = dateTime.atZone(zoneId).toInstant();
        if (targetType == Date.class) {
            return new Date(instant.toEpochMilli());
        }
        if (targetType == java.sql.Date.class) {
            return new java.sql.Date(instant.toEpochMilli());
        }
        if (targetType == Timestamp.class) {
            return new Timestamp(instant.toEpochMilli());
        }
        if (targetType == LocalDate.class) {
            return LocalDate.ofInstant(instant, zoneId);
        }
        throw new UnsupportedOperationException();
    }


    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

}
