package com.ejlchina.searcher.util;

import com.ejlchina.searcher.param.Operator;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MapBuilder {

    public static final String SEPARATOR = "-";
    public static final String OP_SUFFIX = "op";
    public static final String IC_SUFFIX = "ic";
    public static final String SORT = "sort";
    public static final String ORDER = "order";
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String OFFSET = "offset";
    public static final String MAX = "max";

    private static final Map<String, String> CONFIGS = new HashMap<>();

    @FunctionalInterface
    public interface FieldFunction<T, R> extends Function<T, R>, Serializable {  }

    private final Map<FieldFunction<?, ?>, String> cache = new ConcurrentHashMap<>();

    private final Map<String, Object> map;

    public MapBuilder(Map<String, Object> map) {
        this.map = map;
    }

    public static void config(String key, String value) {
        CONFIGS.put(key, value);
    }

    public static String config(String key) {
        String config = CONFIGS.get(key);
        if (config != null) {
            return config;
        }
        return key;
    }

    public MapBuilder put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public <T> MapBuilder val(FieldFunction<T, ?> fieldFn, Object... values) {
        String fieldName = toFieldName(fieldFn);
        String separator = config(SEPARATOR);
        for (int index = 0; index < values.length; index++) {
            map.put(fieldName + separator + index, values[index]);
        }
        return this;
    }

    public <T> MapBuilder op(FieldFunction<T, ?> fieldFn, Operator operator) {
        map.put(toFieldName(fieldFn) + config(SEPARATOR) + config(OP_SUFFIX), operator);
        return this;
    }

    public <T> MapBuilder ic(FieldFunction<T, ?> fieldFn) {
        return ic(fieldFn, true);
    }

    public <T> MapBuilder ic(FieldFunction<T, ?> fieldFn, boolean ignoreCase) {
        map.put(toFieldName(fieldFn) + config(SEPARATOR) + config(IC_SUFFIX), ignoreCase);
        return this;
    }

    public <T> MapBuilder orderBy(FieldFunction<T, ?> fieldFn) {
        return orderBy(fieldFn, true);
    }

    public <T> MapBuilder orderBy(FieldFunction<T, ?> fieldFn, boolean isAsc) {
        map.put(config(SORT), toFieldName(fieldFn));
        map.put(config(ORDER), isAsc ? "asc" : "desc");
        return this;
    }

    public <T> MapBuilder page(int page, int size) {
        map.put(config(PAGE), page);
        map.put(config(SIZE), size);
        return this;
    }

    public <T> MapBuilder limit(int offset, int max) {
        map.put(config(OFFSET), offset);
        map.put(config(MAX), max);
        return this;
    }

    private String toFieldName(FieldFunction<?, ?> fieldFn) {
        String fieldName = cache.get(fieldFn);
        if (fieldName != null) {
            return fieldName;
        }
        try {
            Method wrMethod = fieldFn.getClass().getDeclaredMethod("writeReplace");
            boolean isInaccessible = !wrMethod.isAccessible();
            if (isInaccessible) {
                wrMethod.setAccessible(true);
            }
            SerializedLambda sLambda = (SerializedLambda) wrMethod.invoke(fieldFn);
            if (isInaccessible) {
                wrMethod.setAccessible(false);
            }
            String methodName = sLambda.getImplMethodName();
            if (methodName.startsWith("get") && methodName.length() > 3) {
                fieldName = StringUtils.firstCharToLoweCase(methodName.substring(3));
            }
            if (methodName.startsWith("is") && methodName.length() > 2) {
                fieldName = StringUtils.firstCharToLoweCase(methodName.substring(2));
            }
            if (fieldName != null) {
                cache.put(fieldFn, fieldName);
                return fieldName;
            }
            throw new IllegalStateException("can not convert method [" + methodName + "] to field name");
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("无法反射出字段名", e);
        }
    }

    public Map<String, Object> build() {
        return map;
    }

}
