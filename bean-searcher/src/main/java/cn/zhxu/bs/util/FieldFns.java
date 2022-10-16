package cn.zhxu.bs.util;

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

    /**
     * @param fieldFn 方法引用
     * @param <T> 泛型
     * @return 该方法对应的属性名
     */
    public static <T> String name(FieldFn<T, ?> fieldFn) {
        // 虽然同一个方法在不同地方引用都是不同的 lambda 实例，但同一个地方方法引用在被多次执行时，是同一个 lambda 实例，所以缓存还是有用的
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
            throw new IllegalStateException("Can not resolve the name of method: " + methodName);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Can not resolve the name of " + fieldFn, e);
        }
    }

}
