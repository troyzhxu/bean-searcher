package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.bean.DbType;
import cn.zhxu.bs.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [String | java.util.Date | LocalDate to java.sql.Date] 参数值转换器
 *
 * @author Troy.Zhou @ 2022-06-14
 * @since v3.8.0
 */
public class DateParamConvertor implements FieldConvertor.ParamConvertor {

    static final Pattern DATE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}");

    static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-")
                .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NOT_NEGATIVE)
                .appendPattern("-")
                .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
                .toFormatter();

    /**
     * 转换目标
     */
    public enum Target {

        SQL_DATE,
        LOCAL_DATE

    }

    private Target target = Target.SQL_DATE;

    public DateParamConvertor() { }

    public DateParamConvertor(Target target) {
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        return meta.getDbType() == DbType.DATE && (
                String.class == valueType ||
                        Date.class.isAssignableFrom(valueType) ||
                        LocalDate.class == valueType ||
                        LocalDateTime.class == valueType
        );
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        if (value instanceof String) {
            String s = ((String) value).trim().replaceAll("/", "-");
            if (StringUtils.isBlank(s)) {
                return null;
            }
            Matcher matcher = DATE_PATTERN.matcher(s);
            if (matcher.find()) {
                return toDate(LocalDate.parse(matcher.group(), FORMATTER));
            }
        }
        if (value instanceof Date) {
            java.sql.Date sqlDate = new java.sql.Date(((Date) value).getTime());
            if (target == Target.SQL_DATE) {
                return sqlDate;
            }
            return sqlDate.toLocalDate();
        }
        if (value instanceof LocalDate) {
            return toDate((LocalDate) value);
        }
        if (value instanceof LocalDateTime) {
            return toDate(((LocalDateTime) value).toLocalDate());
        }
        return null;
    }

    private Object toDate(LocalDate date) {
        if (target == Target.SQL_DATE) {
            long days = date.getLong(ChronoField.EPOCH_DAY);
            return new java.sql.Date(TimeUnit.DAYS.toMillis(days));
        }
        return date;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = Objects.requireNonNull(target);
    }

}
