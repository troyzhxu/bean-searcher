package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;

import java.sql.Time;
import java.time.LocalTime;

/**
 * 日期字段转换器
 *
 * @author Troy.Zhou @ 2021-11-12
 * @since v3.1.0
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
