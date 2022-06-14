package com.ejlchina.searcher.convertor;


import com.ejlchina.searcher.ParamResolver;
import com.ejlchina.searcher.bean.DbType;
import com.ejlchina.searcher.util.StringUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [String | java.util.Date | LocalDate to java.sql.Date] 参数值转换器
 *
 * @author Troy.Zhou @ 2022-06-14
 * @since v3.8.0
 */
public class TimeParamConvertor implements ParamResolver.Convertor {

    static final Pattern DATE_PATTERN_1 = Pattern.compile("[0-9]{2}:[0-9]{2}:[0-9]{2}");
    static final Pattern DATE_PATTERN_2 = Pattern.compile("[0-9]{2}:[0-9]{2}");

    static final DateTimeFormatter FORMATTER_1 = DateTimeFormatter.ofPattern("HH:mm:ss");
    static final DateTimeFormatter FORMATTER_2 = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public boolean supports(DbType dbType, Class<?> valueType) {
        return dbType == DbType.TIME && (String.class == valueType || LocalTime.class == valueType);
    }

    @Override
    public Object convert(DbType dbType, Object value) {
        if (value instanceof String) {
            String s = (String) value;
            if (StringUtils.isBlank(s)) {
                return null;
            }
            TemporalQuery<LocalTime> query = TemporalQueries.localTime();
            Matcher matcher1 = DATE_PATTERN_1.matcher(s);
            if (matcher1.find()) {
                return toTime(FORMATTER_1.parse(matcher1.group(), query));
            }
            Matcher matcher2 = DATE_PATTERN_2.matcher(s);
            if (matcher2.find()) {
                return toTime(FORMATTER_2.parse(matcher2.group(), query));
            }
        }
        if (value instanceof LocalTime) {
            return toTime((LocalTime) value);
        }
        return null;
    }

    private java.sql.Time toTime(LocalTime time) {
        return new java.sql.Time(time.getHour(), time.getMinute(), time.getSecond());
    }

}
