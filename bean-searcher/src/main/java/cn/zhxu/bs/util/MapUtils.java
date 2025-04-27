package cn.zhxu.bs.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

public class MapUtils {

    public static final String ARRAY_KEYS = "ARRAY_KEYS." + UUID.randomUUID();

    /**
     * 将一个 value 为数组的 Map 对象，拉平为 value 为单值的 Map 对象
     * @param map 已有 Map 参数
     * @return Map 对象
     */
    public static Map<String, Object> flat(Map<String, String[]> map) {
        return flat(map, true);
    }

    /**
     * 将一个 value 为数组的 Map 对象，拉平为 value 为单值的 Map 对象
     * @param map 已有 Map 参数
     * @param urlDecode 是否进行 URL 解码
     * @return Map 对象
     * @since v4.4.2
     */
    public static Map<String, Object> flat(Map<String, String[]> map, boolean urlDecode) {
        Map<String, Object> newMap = new HashMap<>();
        for (Entry<String, String[]> entry : map.entrySet()) {
            String key = entry.getKey();
            if (key == null) {
                continue;
            }
            String[] values = urlDecode(entry.getValue(), urlDecode);
            if (values == null || values.length == 0) {
                newMap.put(key, null);
            } else if (values.length == 1) {
                newMap.put(key, values[0]);
            } else {
                obtainList(newMap, ARRAY_KEYS).add(key);
                newMap.put(key, values);
            }
        }
        return newMap;
    }

    static String[] urlDecode(String[] values, boolean urlDecode) {
        if (values == null || values.length == 0 || !urlDecode) {
            return values;
        }
        String[] decodedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            try {
                decodedValues[i] = URLDecoder.decode(values[i], Charset.defaultCharset().name());
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        return decodedValues;
    }

    /**
     * 返回一个 lambda Map 参数构造器
     * @return MapBuilder
     */
    public static MapBuilder builder() {
        return builder(new HashMap<>());
    }

    /**
     * 将一个 value 为数组的 Map 对象，拉平为 value 为单值的 Map 对象，并返回一个 lambda Map 参数构造器
     * @param map 已有 Map 参数
     * @return MapBuilder
     */
    public static MapBuilder flatBuilder(Map<String, String[]> map) {
        return new MapBuilder(flat(map));
    }

    /**
     * 将一个 value 为数组的 Map 对象，拉平为 value 为单值的 Map 对象，并返回一个 lambda Map 参数构造器
     * @param map 已有 Map 参数
     * @param urlDecode 是否进行 URL 解码
     * @return MapBuilder
     * @since v4.4.2
     */
    public static MapBuilder flatBuilder(Map<String, String[]> map, boolean urlDecode) {
        return new MapBuilder(flat(map, urlDecode));
    }

    /**
     * 返回一个 lambda Map 参数构造器
     * @param map 已有 Map 参数
     * @return MapBuilder
     */
    public static MapBuilder builder(Map<String, Object> map) {
        return new MapBuilder(map);
    }

    /**
     * 构建只有一个键值对的 Map 对象
     * @param key 键
     * @param value 值
     * @return Map 对象
     * @since v4.0.0
     */
    public static Map<String, Object> of(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    /**
     * 构建只有两个键值对的 Map 对象
     * @param key1 第一个键
     * @param value1 第一个值
     * @param key2 第二个键
     * @param value2 第二个值
     * @return Map 对象
     * @since v4.0.0
     */
    public static Map<String, Object> of(String key1, Object value1, String key2, Object value2) {
        Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    /**
     * 构建只有一个键值对的 Map 对象
     * @param <T> 泛型
     * @param key 键
     * @param value 值
     * @return Map 对象
     * @since v4.0.0
     */
    public static <T> Map<String, Object> of(FieldFns.FieldFn<T, ?> key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(FieldFns.name(key), value);
        return map;
    }

    /**
     * 构建只有两个键值对的 Map 对象
     * @param <T> 泛型
     * @param key1 第一个键
     * @param value1 第一个值
     * @param key2 第二个键
     * @param value2 第二个值
     * @return Map 对象
     * @since v4.0.0
     */
    public static <T> Map<String, Object> of(FieldFns.FieldFn<T, ?> key1, Object value1, FieldFns.FieldFn<T, ?> key2, Object value2) {
        Map<String, Object> map = new HashMap<>();
        map.put(FieldFns.name(key1), value1);
        map.put(FieldFns.name(key2), value2);
        return map;
    }

    /**
     * 从 map 中获取 List, 如果不存在则放入一个空 List
     * @param map Map 映射
     * @param key List 键值
     * @return List
     * @param <T> 泛型
     */
    public static <T> List<T> obtainList(Map<String, Object> map, String key) {
        return obtainList(map, key, true);
    }

    /**
     * 从 map 中获取 List, 如果不存在则放入一个空 List
     * @param map Map 映射
     * @param key List 键值
     * @param createIfNull 不存在时是否创建一个
     * @return List
     * @param <T> 泛型
     */
    public static <T> List<T> obtainList(Map<String, Object> map, String key, boolean createIfNull) {
        Object value = map.get(key);
        List<T> list = null;
        if (value instanceof List) {
            @SuppressWarnings("all")
            List<T> l = (List<T>) value;
            list = l;
        }
        if (list == null && createIfNull) {
            list = new ArrayList<>();
            map.put(key, list);
        }
        return list;
    }

}
