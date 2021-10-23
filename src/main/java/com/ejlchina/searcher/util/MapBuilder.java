package com.ejlchina.searcher.util;

import com.ejlchina.searcher.param.Operator;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

public class MapBuilder {

    /**
     * 参数名分割符
     */
    private static String paramSeparator = "-";

    /**
     * 过滤运算符参数名后缀
     */
    private static String operationSuffix = "op";


    @FunctionalInterface
    public interface FieldFunction<T, R> extends Function<T, R>, Serializable {  }


    private final Map<String, Object> map;

    public MapBuilder(Map<String, Object> map) {
        this.map = map;
    }

    public static void config(String paramSeparator, String operationSuffix) {
        MapBuilder.paramSeparator = paramSeparator;
        MapBuilder.operationSuffix = operationSuffix;
    }

    public MapBuilder put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public <T> MapBuilder put(FieldFunction<T, ?> fieldFn, Object value) {
        String fieldName = toFieldName(fieldFn);
        map.put(fieldName, value);
        return this;
    }

    public <T> MapBuilder put(FieldFunction<T, ?> fieldFn, int index, Object value) {
        String fieldName = toFieldName(fieldFn);
        map.put(fieldName + paramSeparator + index, value);
        return this;
    }

    public <T> MapBuilder put(FieldFunction<T, ?> fieldFn, Operator operator) {
        String fieldName = toFieldName(fieldFn);
        map.put(fieldName + paramSeparator + operationSuffix, operator);
        return this;
    }

    private String toFieldName(FieldFunction<?, ?> fieldFn) {
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
                return StringUtils.firstCharToLoweCase(methodName.substring(3));
            }
            if (methodName.startsWith("is") && methodName.length() > 2) {
                return StringUtils.firstCharToLoweCase(methodName.substring(2));
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
