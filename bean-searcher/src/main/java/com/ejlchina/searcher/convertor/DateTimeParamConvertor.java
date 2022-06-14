package com.ejlchina.searcher.convertor;


import com.ejlchina.searcher.ParamResolver;
import com.ejlchina.searcher.bean.DbType;
import com.ejlchina.searcher.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * [String | java.util.Date | LocalDate to java.sql.Date] 参数值转换器
 *
 * @author Troy.Zhou @ 2022-06-14
 * @since v3.8.0
 */
public class DateTimeParamConvertor implements ParamResolver.Convertor {

    static final Pattern DATETIME_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}");

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean supports(DbType dbType, Class<?> valueType) {
        return dbType == DbType.DATETIME && (String.class == valueType || Date.class == valueType || LocalDate.class == valueType || LocalDateTime.class == valueType);
    }

    @Override
    public Object convert(DbType dbType, Object value) {
        if (value instanceof String) {
            String s = ((String) value).trim().replaceAll("/", "-");
            if (StringUtils.isBlank(s)) {
                return null;
            }
            String datetime = normalize(s);
            if (DATETIME_PATTERN.matcher(datetime).matches()) {
                TemporalAccessor accessor = FORMATTER.parse(datetime);
                long days = accessor.getLong(ChronoField.EPOCH_DAY);
                long mills = accessor.getLong(ChronoField.MILLI_OF_DAY);
                return new Timestamp(TimeUnit.DAYS.toMillis(days) + mills);
            }
        }
        if (value instanceof Date) {
            return new Timestamp(((Date) value).getTime());
        }
        if (value instanceof LocalDate) {
            long days = ((LocalDate) value).getLong(ChronoField.EPOCH_DAY);
            return new Timestamp(TimeUnit.DAYS.toMillis(days));
        }
        if (value instanceof LocalDateTime) {
            LocalDateTime dateTime = (LocalDateTime) value;
            long days = dateTime.getLong(ChronoField.EPOCH_DAY);
            long mills = dateTime.getLong(ChronoField.MILLI_OF_DAY);
            return new Timestamp(TimeUnit.DAYS.toMillis(days) + mills);
        }
        return null;
    }

    private String normalize(String datetime) {
        int len = datetime.length();
        if (len == 16) {
            return datetime + ":00";
        }
        if (len == 13) {
            return datetime + ":00:00";
        }
        if (len == 10) {
            return datetime + " 00:00:00";
        }
        return datetime;
    }

}
