package com.ejlchina.searcher.convertor;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;
import com.ejlchina.searcher.SearchException;
import com.ejlchina.searcher.implement.DefaultMapSearcher;
import com.ejlchina.searcher.util.ObjKey2;
import com.ejlchina.searcher.util.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 日期/时间格式化字段转换器
 * 该转换器可将数据库取出的 Date 对象字段 转换为 格式化的日期字符串
 * 与 {@link DefaultMapSearcher } 配合使用
 *
 * v3.0.0 支持 {@link Date } 及其子类的 日期格式化
 * v3.0.1 支持 {@link Temporal } 及其子类的 日期格式化
 *
 * @author Troy.Zhou @ 2021-11-03
 * @since v3.0.0（v3.8.0 之前在 com.ejlchina.searcher.implement 包下）
 */
public class DateFormatFieldConvertor implements FieldConvertor.MFieldConvertor {

    public static final Pattern DATE_PATTERN = Pattern.compile("[yMd]+");
    public static final Pattern TIME_PATTERN = Pattern.compile("[HhmsS]+");

    private final Map<String, Formatter> formatMap = new ConcurrentHashMap<>();

    /**
     * 时区
     */
    private ZoneId zoneId = ZoneId.systemDefault();

    /**
     * 添加一个日期/时间格式，例如（优先级以此递减）：
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
     * @param format 日期格式，如：yyyy-MM-dd，传入 null / '' 时表示该 scope 下的日期字段不进行格式化
     * @since v3.0.1
     */
    public void setFormat(String scope, String format) {
        if (StringUtils.isBlank(format) || "NULL".equalsIgnoreCase(format.trim())) {
            formatMap.put(scope, new Formatter(null));
            return;
        }
        try {
            formatMap.put(scope, new Formatter(format));
        } catch (IllegalArgumentException e) {
            throw new SearchException("您配置了一个错误的日期/时间格式：[scope: " + scope + ", format: " + format + "]", e);
        }
    }

    private final Map<ObjKey2, Formatter> cache = new ConcurrentHashMap<>();

    private final Formatter NULL_FORMATTER = new Formatter(null);

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        ObjKey2 key = new ObjKey2(meta, valueType);
        Formatter formatter = cache.get(key);
        if (formatter == NULL_FORMATTER) {
            return false;
        }
        if (formatter != null) {
            return formatter.pattern != null;
        }
        if (Date.class.isAssignableFrom(valueType) || Temporal.class.isAssignableFrom(valueType)) {
            Formatter f = findFormatter(meta, valueType);
            if (f != null) {
                cache.put(key, f);
                return f.pattern != null;
            }
        }
        cache.put(key, NULL_FORMATTER);
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        ObjKey2 key = new ObjKey2(meta, value.getClass());
        Formatter formatter = cache.get(key);
        if (formatter != null) {
            return formatter.format(value);
        }
        throw new IllegalStateException("The supports(FieldMeta, Class<?>) method must be called first and return true before convert(FieldMeta, Object) method can be called");
    }

    public class Formatter {

        private final String pattern;
        private final DateTimeFormatter formatter;

        public Formatter(String pattern) {
            if (pattern != null) {
                formatter = DateTimeFormatter.ofPattern(pattern);
            } else {
                formatter = null;
            }
            this.pattern = pattern;
        }

        public Object format(Object value) {
            if (formatter != null) {
                if (value instanceof java.sql.Date) {
                    LocalDate localDate = ((java.sql.Date) value).toLocalDate();
                    return formatter.format(localDate);
                }
                if (value instanceof java.sql.Time) {
                    LocalTime localTime = ((java.sql.Time) value).toLocalTime();
                    return formatter.format(localTime);
                }
                if (value instanceof Date) {
                    // 注意 java.sql.Date/Time 的 toInstant 方法会报错
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

        public boolean supports(Class<?> dateType) {
            return pattern == null || (
                    (dateType != LocalTime.class && dateType != java.sql.Time.class && dateType != LocalDate.class && dateType != java.sql.Date.class) ||
                    (dateType == LocalTime.class || dateType == java.sql.Time.class) && !DATE_PATTERN.matcher(pattern).find() ||
                    (dateType == LocalDate.class || dateType == java.sql.Date.class) && !TIME_PATTERN.matcher(pattern).find()
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

    private final Map<Class<?>, List<String>> typeNameMap = new ConcurrentHashMap<>();

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
