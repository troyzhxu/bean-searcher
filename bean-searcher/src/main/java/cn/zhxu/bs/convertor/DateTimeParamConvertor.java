package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.bean.DbType;
import cn.zhxu.bs.util.StringUtils;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoField.EPOCH_DAY;

/**
 * [String | java.util.Date | LocalDate to java.sql.Date] 参数值转换器
 *
 * @author Troy.Zhou @ 2022-06-14
 * @since v3.8.0
 */
public class DateTimeParamConvertor implements FieldConvertor.ParamConvertor {

    static final Pattern DATETIME_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}");

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 时区
     */
    private TimeZone timeZone = TimeZone.getDefault();

    /**
     * 转换目标
     */
    public enum Target {

        SQL_TIMESTAMP,
        LOCAL_DATE_TIME

    }

    private Target target = Target.SQL_TIMESTAMP;

    public DateTimeParamConvertor() { }

    public DateTimeParamConvertor(Target target) {
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        return meta.getDbType() == DbType.DATETIME && (
                String.class == valueType ||
                        Date.class.isAssignableFrom(valueType) ||
                        LocalDate.class == valueType ||
                        LocalDateTime.class == valueType
        );
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        if (value instanceof String) {
            String str = ((String) value).trim();
            if (StringUtils.isBlank(str)) {
                return null;
            }
            if (StringUtils.isNumeric(str)) {
                // 处理字符串形式的时间戳
                return toTargetType(Long.parseLong(str));
            }
            String datetime = normalize(str.replaceAll("/", "-"));
            if (DATETIME_PATTERN.matcher(datetime).matches()) {
                TemporalAccessor accessor = FORMATTER.parse(datetime);
                LocalDateTime dateTime = LocalDate.ofEpochDay(accessor.getLong(EPOCH_DAY))
                        .atTime(LocalTime.ofSecondOfDay(accessor.getLong(ChronoField.SECOND_OF_DAY)));
                return toTargetType(dateTime);
            }
            return null;
        }
        if (value instanceof Date) {
            if (target == Target.LOCAL_DATE_TIME) {
                // 注意：java.sql.Date 的 toInstant() 方法会抛异常
                if (value instanceof java.sql.Date) {
                    LocalDate localDate = ((java.sql.Date) value).toLocalDate();
                    return LocalDateTime.of(localDate, LocalTime.of(0, 0, 0, 0));
                }
                return LocalDateTime.ofInstant(((Date) value).toInstant(), getZoneId());
            }
            return new Timestamp(((Date) value).getTime());
        }
        if (value instanceof LocalDate) {
            return toTargetType(((LocalDate) value).atTime(0, 0, 0));
        }
        if (value instanceof LocalDateTime) {
            return toTargetType((LocalDateTime) value);
        }
        if (value instanceof Long) {
            return toTargetType((Long) value);
        }
        return null;
    }

    protected Object toTargetType(long epochMilli) {
        if (target == Target.SQL_TIMESTAMP) {
            return new Timestamp(epochMilli);
        }
        Instant instant = Instant.ofEpochMilli(epochMilli);
        return LocalDateTime.ofInstant(instant, getZoneId());
    }

    protected Object toTargetType(LocalDateTime dateTime) {
        if (target == Target.SQL_TIMESTAMP) {
            Instant instant = dateTime.toInstant(getZoneOffset());
            return new Timestamp(instant.toEpochMilli());
        }
        return dateTime;
    }

    protected String normalize(String datetime) {
        int len = datetime.length();
        if (len == 19) {
            return datetime + ".000";
        }
        if (len == 16) {
            return datetime + ":00.000";
        }
        if (len == 13) {
            return datetime + ":00:00.000";
        }
        if (len == 10) {
            return datetime + " 00:00:00.000";
        }
        if (len == 7) {
            return datetime + "-01 00:00:00.000";
        }
        if (len == 4) {
            return datetime + "-01-01 00:00:00.000";
        }
        return datetime;
    }

    private transient ZoneId zoneId;
    private transient ZoneOffset offset;

    public ZoneId getZoneId() {
        if (zoneId != null) {
            return zoneId;
        }
        return zoneId = timeZone.toZoneId();
    }

    public void setZoneId(ZoneId zoneId) {
        if (zoneId != null) {
            timeZone = TimeZone.getTimeZone(zoneId);
            this.zoneId = zoneId;
            this.offset = null;
        }
    }

    public ZoneOffset getZoneOffset() {
        if (offset != null) {
            return offset;
        }
        return offset = ZoneOffset.ofTotalSeconds(timeZone.getRawOffset() / 1000);
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = Objects.requireNonNull(target);
    }

}
