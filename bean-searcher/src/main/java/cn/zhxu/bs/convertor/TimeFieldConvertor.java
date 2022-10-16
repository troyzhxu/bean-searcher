package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;

import java.sql.Time;
import java.time.LocalTime;

/**
 * 时间字段转换器: java.sql.Time、LocalTime 之间的转换
 *
 * @author Troy.Zhou @ 2022-02-14
 * @since v3.5.0（v3.8.0 之前在 com.ejlchina.searcher.implement 包下）
 */
public class TimeFieldConvertor implements FieldConvertor.BFieldConvertor {

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        if (valueType == Time.class || valueType == LocalTime.class) {
            Class<?> type = meta.getType();
            return type == Time.class || type == LocalTime.class;
        }
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        Class<?> type = meta.getType();
        if (value instanceof Time && type == LocalTime.class) {
            return ((Time) value).toLocalTime();
        }
        if (value instanceof LocalTime && type == Time.class) {
            return Time.valueOf((LocalTime) value);
        }
        return value;
    }

}
