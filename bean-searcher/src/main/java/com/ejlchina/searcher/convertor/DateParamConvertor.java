package com.ejlchina.searcher.convertor;


import com.ejlchina.searcher.ParamResolver;
import com.ejlchina.searcher.bean.DbType;
import com.ejlchina.searcher.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [String | java.util.Date | LocalDate to java.sql.Date] 参数值转换器
 *
 * @author Troy.Zhou @ 2022-06-14
 * @since v3.8.0
 */
public class DateParamConvertor implements ParamResolver.Convertor {

    static final Pattern DATE_PATTERN_1 = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
    static final Pattern DATE_PATTERN_2 = Pattern.compile("[0-9]{4}/[0-9]{2}/[0-9]{2}");

    static final DateTimeFormatter FORMATTER_1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static final DateTimeFormatter FORMATTER_2 = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public boolean supports(DbType dbType, Class<?> valueType) {
        return dbType == DbType.DATE && (String.class == valueType || Date.class == valueType || LocalDate.class == valueType);
    }

    @Override
    public Object convert(DbType dbType, Object value) {
        if (value instanceof String) {
            String s = (String) value;
            if (StringUtils.isBlank(s)) {
                return null;
            }
            TemporalQuery<LocalDate> query = TemporalQueries.localDate();
            Matcher matcher1 = DATE_PATTERN_1.matcher(s);
            if (matcher1.find()) {
                return toDate(FORMATTER_1.parse(matcher1.group(), query));
            }
            Matcher matcher2 = DATE_PATTERN_2.matcher(s);
            if (matcher2.find()) {
                return toDate(FORMATTER_2.parse(matcher2.group(), query));
            }
        }
        if (value instanceof Date) {
            return new java.sql.Date(((Date) value).getTime());
        }
        if (value instanceof LocalDate) {
            return toDate((LocalDate) value);
        }
        return null;
    }

    private java.sql.Date toDate(LocalDate date) {
        long days = date.getLong(ChronoField.EPOCH_DAY);
        return new java.sql.Date(TimeUnit.DAYS.toMillis(days));
    }

}
