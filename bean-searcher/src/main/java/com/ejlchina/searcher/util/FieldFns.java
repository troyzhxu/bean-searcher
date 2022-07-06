package com.ejlchina.searcher.util;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 实体类方法引用工具类，用法：
 * <pre>{@code
 * String name = FieldFns.name(User::getName);
 * }</pre>
 * @since v3.8.1
 * @author Troy.Zhou @ 2017-03-20
 */
public class FieldFns {

    @FunctionalInterface
    public interface FieldFn<T, R> extends Function<T, R>, Serializable {  }

    private static final Map<FieldFn<?, ?>, String> cache = new ConcurrentHashMap<>();

    public static String name(FieldFn<?, ?> fieldFn) {
        String name = cache.get(fieldFn);
        if (name != null) {
            return name;
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
                name = StringUtils.firstCharToLoweCase(methodName.substring(3));
            }
            if (methodName.startsWith("is") && methodName.length() > 2) {
                name = StringUtils.firstCharToLoweCase(methodName.substring(2));
            }
            if (name != null) {
                cache.put(fieldFn, name);
                return name;
            }
            throw new IllegalStateException("can not convert method [" + methodName + "] to field name");
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("无法反射出字段名", e);
        }
    }

}
