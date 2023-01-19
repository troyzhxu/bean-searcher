package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;

import java.sql.Timestamp;
import java.time.*;
import java.util.Date;

/**
 * Long => Date 字段转换器，用于兼容 Sqlite 再某些情况下日期字段会返回 Long 值的情况
 *
 * @author Troy.Zhou @ 2023-01-19
 * @since v4.0.0
 */
public class Long2DateFieldConvertor implements FieldConvertor.BFieldConvertor {

    /**
     * 默认时区
     */
    private ZoneId zoneId = ZoneId.systemDefault();

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        if (valueType == Long.class) {
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
        Class<?> targetType = meta.getType();
        Long epochMilli = (Long) value;
        if (targetType == Date.class) {
            return new Date(epochMilli);
        }
        if (targetType == java.sql.Date.class) {
            return new java.sql.Date(epochMilli);
        }
        if (targetType == Timestamp.class) {
            return new Timestamp(epochMilli);
        }
        Instant instant = Instant.ofEpochMilli(epochMilli);
        if (targetType == LocalDateTime.class) {
            return LocalDateTime.ofInstant(instant, zoneId);
        }
        if (targetType == LocalDate.class) {
            // 以下代码用于兼容 JDK8, 因为 JDK8 没有 LocalDate.ofInstant(..) 方法
            ZoneOffset offset = zoneId.getRules().getOffset(instant);
            long localSecond = instant.getEpochSecond() + offset.getTotalSeconds();
            long localEpochDay = Math.floorDiv(localSecond, 86400L);
            return LocalDate.ofEpochDay(localEpochDay);
        }
        throw new IllegalStateException("The supports(FieldMeta, Class<?>) method must be called first and return true before invoking convert(FieldMeta, Object) method.");
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

}
