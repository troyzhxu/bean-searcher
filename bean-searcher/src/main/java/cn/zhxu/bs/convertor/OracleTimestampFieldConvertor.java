package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import oracle.sql.TIMESTAMP;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用于兼容 Oracle 的 TIMESTAMP 类型
 *
 * @author Troy.Zhou @ 2022-10-02
 * @since v4.4.0
 */
public class OracleTimestampFieldConvertor implements FieldConvertor.BFieldConvertor {

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        if (valueType == TIMESTAMP.class) {
            Class<?> targetType = meta.getType();
            return (
                    targetType == Date.class ||
                            targetType == java.sql.Date.class ||
                            targetType == Timestamp.class ||
                            targetType == LocalDateTime.class ||
                            targetType == LocalDate.class ||
                            targetType == Instant.class
            );
        }
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        Class<?> targetType = meta.getType();
        TIMESTAMP timestamp = (TIMESTAMP) value;
        try {
            if (targetType == java.sql.Date.class) {
                return timestamp.dateValue();
            }
            if (targetType == Timestamp.class) {
                return timestamp.timestampValue();
            }
            if (targetType == LocalDateTime.class) {
                return timestamp.localDateTimeValue();
            }
            if (targetType == Instant.class) {
                return Instant.from(timestamp.localDateTimeValue());
            }
            if (targetType == LocalDate.class) {
                return timestamp.localDateValue();
            }
            if (targetType == Date.class) {
                return new Date(timestamp.timestampValue().getTime());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Fail to convert oracle.sql.TIMESTAMP to " + targetType, e);
        }
        throw new IllegalStateException("Unsupported targetType: " + targetType);
    }

}
