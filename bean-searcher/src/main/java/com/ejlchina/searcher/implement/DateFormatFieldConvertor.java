package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日期格式化字段转换器
 * 该转换器可将数据库取出的 Date 对象字段 转换为 格式化的日期字符串
 * 常与 {@link com.ejlchina.searcher.MapSearcher } 配合使用
 *
 * @author Troy.Zhou @ 2021-11-03
 * @since v3.0.0
 */
public class DateFormatFieldConvertor implements FieldConvertor {

    private final Map<String, Pattern> formatMap = new ConcurrentHashMap<>();

    private final ThreadLocal<Map<String, DateFormat>> local = new ThreadLocal<>();

    public static class Pattern {

        final String format;

        public Pattern(String format) {
            this.format = format;
        }

    }

    /**
     * 添加一个日期格式，例如：
     * <pre>
     * addFormat("com.example.bean.User.dateCreated", "yyyy-MM-dd");
     * 只对 com.example.bean.User 的 dateCreated 字段起作用，优先级最高
     * addFormat("com.example.bean.User", "yyyy-MM-dd HH");
     * 可对 com.example.bean.User 类的所有字段起作用，但优先级比上一个低一点
     * addFormat("com.example.bean", "yyyy-MM-dd HH:mm");
     * 可对 com.example.bean 包下的所有类字段起作用，优先级比上一个再低一点
     * addFormat("com.example", "yyyy-MM-dd HH:mm:ss");
     * 可对 com.example 包下的所有类字段起作用，优先级比上一个更低一点
     * addFormat("com", "yyyy-MM-dd HH:mm:ss:SSS");
     * 可对 com 包下的所有类字段起作用，但使用优先级最低
     * </pre>
     * @param scope 生效范围（越精确，优先级越高）
     * @param format 日期格式，如：yyyy-MM-dd，传入 null 时表示该 scope 下的日期字段不进行格式化
     */
    public void setFormat(String scope, String format) {
        formatMap.put(scope, new Pattern(format));
    }

    /**
     * Deprecated from v3.1.0
     * 请使用 setFormat(String scope, String format) 方法
     * @param scope 生效范围（越精确，优先级越高）
     * @param format 日期格式，如：yyyy-MM-dd，传入 null 时表示该 scope 下的日期字段不进行格式化
     */
    @Deprecated
    public void addFormat(String scope, String format) {
        setFormat(scope, format);
    }

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType, Class<?> targetType) {
        return Date.class.isAssignableFrom(valueType) && targetType == null;
    }

    @Override
    public Object convert(FieldMeta meta, Object value, Class<?> targetType) {
        Pattern pattern = getPattern(meta);
        if (pattern == null || pattern.format == null) {
            // 该字段不需要格式化
            return value;
        }
        return getFormat(pattern.format).format((Date) value);
    }

    protected Pattern getPattern(FieldMeta meta) {
        Class<?> clazz = meta.getBeanMeta().getBeanClass();
        String className = clazz.getName();
        // 以 类全名.属性名 为 key 寻找 pattern
        Pattern pattern = formatMap.get(className + "." + meta.getName());
        if (pattern != null) {
            return pattern;
        }
        // 以 类全名 为 key 寻找 pattern
        pattern = formatMap.get(className);
        if (pattern != null) {
            return pattern;
        }
        // 以 包名 为 key 寻找 pattern
        String pkgName = className;
        int index = className.lastIndexOf('.');
        while (index > 0) {
            pkgName = pkgName.substring(0, index);
            pattern = formatMap.get(pkgName);
            if (pattern != null) {
                return pattern;
            }
            index = pkgName.lastIndexOf('.');
        }
        return null;
    }

    private DateFormat getFormat(String pattern) {
        Map<String, DateFormat> cache = local.get();
        if (cache == null) {
            cache = new HashMap<>();
            local.set(cache);
        }
        DateFormat format = cache.get(pattern);
        if (format == null) {
            format = new SimpleDateFormat(pattern);
            cache.put(pattern, format);
        }
        return format;
    }

    public Map<String, Pattern> getFormatMap() {
        return formatMap;
    }

}
