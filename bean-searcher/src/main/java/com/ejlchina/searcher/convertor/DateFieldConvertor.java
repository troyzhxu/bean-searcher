package com.ejlchina.searcher.convertor;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;

import java.sql.Timestamp;
import java.time.*;
import java.util.Date;

/**
 * 日期字段转换器
 *
 * @author Troy.Zhou @ 2021-11-12
 * @since v3.1.0（v3.8.0 之前在 com.ejlchina.searcher.implement 包下）
 */
public class DateFieldConvertor implements FieldConvertor.BFieldConvertor {

    /**
     * 时区
     */
    private ZoneId zoneId = ZoneId.systemDefault();

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        if (
                valueType == Date.class ||
                valueType == java.sql.Date.class ||
                valueType == Timestamp.class ||
                valueType == LocalDateTime.class ||
                valueType == LocalDate.class
        ) {
            Class<?> targetType = meta.getType();
            return (
                    targetType == Date.class ||
                    targetType == java.sql.Date.class ||
                    targetType == Timestamp.class ||
                    targetType == LocalDateTime.class ||
                    targetType == LocalDate.class
            );
        }
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        return convert(meta.getType(), value);
    }

    public Object convert(Class<?> targetType, Object value) {
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
                return toLocalDate(date.toInstant());
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
            return toLocalDate(instant);
        }
        throw new UnsupportedOperationException();
    }

    // 该方法用于兼容 JDK8, 因为 JDK8 没有 LocalDate.ofInstant() 方法
    protected LocalDate toLocalDate(Instant instant) {
        ZoneOffset offset = zoneId.getRules().getOffset(instant);
        long localSecond = instant.getEpochSecond() + offset.getTotalSeconds();
        long localEpochDay = Math.floorDiv(localSecond, 86400L);
        return LocalDate.ofEpochDay(localEpochDay);
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

}
