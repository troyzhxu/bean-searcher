package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日期格式化字段转换器
 * 该转换器可将数据库取出的 Date 对象字段 转换为 格式化的日期字符串
 * 常用于 {@link com.ejlchina.searcher.implement.DefaultMapSearcher } 内
 *
 * @author Troy.Zhou @ 2021-11-03
 * @since v3.0.0
 */
public class DateFormatFieldConvertor implements FieldConvertor {

    private Map<String, String> patternMap = new ConcurrentHashMap<>();

    private final ThreadLocal<Map<String, DateFormat>> local = new ThreadLocal<>();

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType, Class<?> targetType) {
        return Date.class.isAssignableFrom(valueType) && targetType == null;
    }

    @Override
    public Object convert(FieldMeta meta, Object value, Class<?> targetType) {
        String pattern = getPattern(meta);
        if (pattern == null) {
            // 该字段不需要格式化
            return value;
        }
        return getFormat(pattern).format((Date) value);
    }

    protected String getPattern(FieldMeta meta) {
        Class<?> clazz = meta.getBeanMeta().getBeanClass();
        String className = clazz.getName();
        // 以 类全名.属性名 为 key 寻找 pattern
        String pattern = patternMap.get(className + "." + meta.getName());
        if (pattern != null) {
            return pattern;
        }
        // 以 类全名 为 key 寻找 pattern
        pattern = patternMap.get(className);
        if (pattern != null) {
            return pattern;
        }
        // 以 包名 为 key 寻找 pattern
        String pkgName = className;
        int index = className.lastIndexOf('.');
        while (index > 0) {
            pkgName = pkgName.substring(0, index);
            pattern = patternMap.get(pkgName);
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

    public Map<String, String> getPatternMap() {
        return patternMap;
    }

    public void setPatternMap(Map<String, String> patternMap) {
        this.patternMap = Objects.requireNonNull(patternMap);
    }

    public void addPattern(String key, String pattern) {
        patternMap.put(key, pattern);
    }

}
