package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;

import java.sql.Time;
import java.time.LocalTime;

/**
 * 时间字段转换器: java.sql.Time -> LocalTime
 *
 * @author Troy.Zhou @ 2022-02-14
 * @since v3.5.0
 */
public class TimeFieldConvertor implements FieldConvertor.BFieldConvertor {

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        return valueType == Time.class && meta.getType() == LocalTime.class;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        return ((Time) value).toLocalTime();
    }

}
