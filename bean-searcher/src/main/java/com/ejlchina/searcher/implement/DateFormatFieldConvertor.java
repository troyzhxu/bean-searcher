package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 日期格式化字段转换器
 * 该转换器可将数据库取出的 Date 对象字段 转换为 格式化的日期字符串
 * 与 {@link DefaultMapSearcher } 配合使用
 *
 * v3.0.0 支持 {@link Date } 及其子类的 日期格式化
 * v3.0.1 支持 {@link Temporal } 及其子类的 日期格式化
 *
 * @author Troy.Zhou @ 2021-11-03
 * @since v3.0.0
 */
public class DateFormatFieldConvertor implements FieldConvertor.MFieldConvertor {

    public static final Pattern DATE_PATTERN = Pattern.compile("[yMd]+");
    public static final Pattern TIME_PATTERN = Pattern.compile("[HhmsS]+");

    private final Map<String, Formatter> formatMap = new ConcurrentHashMap<>();

    private final Map<Class<?>, List<String>> typeNameMap = new ConcurrentHashMap<>();


    /**
     * 时区
     */
    private ZoneId zoneId = ZoneId.systemDefault();

    /**
     * 添加一个日期格式，例如（优先级以此递减）：
     * <pre>
     * setFormat("demo.User.dateCreated", "yyyy-MM-dd");
     *  指定 demo.User 的 dateCreated 字段的格式
     * setFormat("demo.User:LocalTime", "HH:mm:ss");
     *  指定 demo.User 类的 LocalTime 类型的字段的格式
     * setFormat("demo.User", "yyyy-MM-dd HH");
     *  指定 demo.User 类的其它字段的格式
     * setFormat("demo:LocalDate", "yyyy-MM-dd");
     *  指定 demo 包下的 LocalDate 类型的字段的格式
     * setFormat("demo", "yyyy-MM-dd HH:mm:ss:SSS");
     *  指定 demo 包下的其它日期字段的格式
     * </pre>
     *
     * @param scope 生效范围，可以是 全类名.字段名、全类名:字段类型名、包名:字段类型名 或 包名，范围越小，使用优先级越高
     * @param format 日期格式，如：yyyy-MM-dd，传入 null 时表示该 scope 下的日期字段不进行格式化
     * @since v3.0.1
     */
    public void setFormat(String scope, String format) {
        formatMap.put(scope, new Formatter(format));
    }

    /**
     * Deprecated from v3.0.1
     * 请使用 {@link #setFormat(String scope, String format) } 方法
     * @param scope 生效范围（越精确，优先级越高）
     * @param format 日期格式，如：yyyy-MM-dd，传入 null 时表示该 scope 下的日期字段不进行格式化
     */
    @Deprecated
    public void addFormat(String scope, String format) {
        setFormat(scope, format);
    }

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        return Date.class.isAssignableFrom(valueType) || Temporal.class.isAssignableFrom(valueType);
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        Formatter formatter = findFormatter(meta, value.getClass());
        return formatter != null ? formatter.format(value) : value;
    }

    class Formatter {

        private final String pattern;
        private final DateTimeFormatter formatter;

        Formatter(String pattern) {
            if (pattern != null) {
                formatter = DateTimeFormatter.ofPattern(pattern);
            } else {
                formatter = null;
            }
            this.pattern = pattern;
        }

        Object format(Object value) {
            if (formatter != null) {
                if (value instanceof Date) {
                    Instant instant = ((Date) value).toInstant();
                    LocalDateTime dateTime = LocalDateTime.ofInstant(instant, zoneId);
                    return formatter.format(dateTime);
                }
                if (value instanceof Instant) {
                    LocalDateTime dateTime = LocalDateTime.ofInstant((Instant) value, zoneId);
                    return formatter.format(dateTime);
                }
                if (value instanceof Temporal) {
                    return formatter.format(((Temporal) value));
                }
            }
            return value;
        }

        boolean supports(Class<?> dateType) {
            return pattern == null || (
                    dateType != LocalTime.class || !DATE_PATTERN.matcher(pattern).matches()
            ) && (
                    dateType != LocalDate.class || !TIME_PATTERN.matcher(pattern).matches()
            );
        }

    }

    private Formatter findFormatter(FieldMeta meta, Class<?> type) {
        Class<?> clazz = meta.getBeanMeta().getBeanClass();
        String pathName = clazz.getName();
        // 以 [类全名.属性名] 为 key 寻找 formatter
        Formatter formatter = formatMap.get(pathName + "." + meta.getName());
        if (formatter != null && formatter.supports(type)) {
            return formatter;
        }
        List<String> typeNames = getTypeNames(type);
        while (true) {
            for (String typeName : typeNames) {
                // 以 [类全名/包名:字段类型名/父] 为 key 寻找 formatter
                formatter = formatMap.get(pathName + ":" + typeName);
                if (formatter != null && formatter.supports(type)) {
                    return formatter;
                }
            }
            // 以 [类全名/包名] 为 key 寻找 formatter
            formatter = formatMap.get(pathName);
            if (formatter != null && formatter.supports(type)) {
                return formatter;
            }
            int index = pathName.lastIndexOf('.');
            if (index > -1) {
                pathName = pathName.substring(0, index);
            } else {
                return null;
            }
        }
    }

    private List<String> getTypeNames(Class<?> type) {
        List<String> names = typeNameMap.computeIfAbsent(type, k -> new ArrayList<>(3));
        if (names.isEmpty()) {
            while (type != null && type != Object.class) {
                names.add(type.getSimpleName());
                type = type.getSuperclass();
            }
        }
        return names;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = Objects.requireNonNull(zoneId);
    }

}
