package com.ejlchina.searcher.util;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.param.FieldParam;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.ejlchina.searcher.util.MapBuilder.FIELD_PARAM;


/**
 * 检索参数构建器基类
 * @param <B> 泛型
 */
public class Builder<B extends Builder<B>> {

    @FunctionalInterface
    public interface FieldFn<T, R> extends Function<T, R>, Serializable {  }

    /**
     * 根组，根组的条件总是会被用到
     */
    public static final String ROOT_GROUP = "$";

    private final Map<FieldFn<?, ?>, String> cache = new ConcurrentHashMap<>();

    protected final Map<String, Object> map;

    protected String group = ROOT_GROUP;

    private FieldParam fieldParam = null;

    public Builder(Map<String, Object> map) {
        this.map = map;
    }

    /**
     * 指定某个字段的检索值
     * @param fieldFn 字段表达式
     * @param values 检索值，集合
     * @param <T> 泛型
     * @return MapBuilder
     * @since 3.5.2
     */
    public <T> B field(FieldFn<T, ?> fieldFn, Collection<?> values) {
        return field(fieldFn, values.toArray());
    }

    /**
     * 指定某个字段的检索值
     * @param fieldFn 字段表达式
     * @param values 检索值，可多个
     * @param <T> 泛型
     * @return MapBuilder
     */
    public <T> B field(FieldFn<T, ?> fieldFn, Object... values) {
        return field(toFieldName(fieldFn), values);
    }

    /**
     * 指定某个字段的检索值
     * @param fieldName 实体类属性名
     * @param values 检索值，集合
     * @param <T> 泛型
     * @return MapBuilder
     * @since 3.5.2
     */
    public <T> B field(String fieldName, Collection<?> values) {
        return field(fieldName, values.toArray());
    }

    /**
     * 指定某个字段的检索值
     * @param fieldName 实体类属性名
     * @param values 检索值，可多个
     * @return MapBuilder
     */
    @SuppressWarnings("unchecked")
    public B field(String fieldName, Object... values) {
        if (fieldName != null) {
            List<FieldParam.Value> pValues = new ArrayList<>();
            for (int index = 0; index < values.length; index++) {
                pValues.add(new FieldParam.Value(values[index], index));
            }
            String field = fieldName.trim();
            fieldParam = new FieldParam(field, pValues);
            if (group != null) {
                map.put(group + FIELD_PARAM + field, fieldParam);
            }
            // 如果是 根组，则向 Map 里放入两个 key, 以保证该条件总是被用到
            if (ROOT_GROUP.equals(group)) {
                map.put(FIELD_PARAM + field, fieldParam);
            }
        }
        return (B) this;
    }

    /**
     * 指定上个字段的运算符
     * @param operator 检索运算符
     * @return MapBuilder
     */
    public B op(String operator) {
        return fieldOp(operator);
    }

    /**
     * 指定上个字段的运算符
     * @param operator 检索运算符
     * @return MapBuilder
     */
    public B op(Class<? extends FieldOp> operator) {
        return fieldOp(operator);
    }

    /**
     * 指定上个字段的运算符
     * @param operator 检索运算符
     * @return MapBuilder
     */
    public B op(FieldOp operator) {
        return fieldOp(operator);
    }

    @SuppressWarnings("unchecked")
    private B fieldOp(Object operator) {
        if (fieldParam == null) {
            throw new IllegalStateException("the method [ op(...) ] must go after [ field(...) ] method");
        }
        fieldParam.setOperator(operator);
        return (B) this;
    }

    /**
     * 指定上个字段检索时忽略大小写
     * @return MapBuilder
     */
    public B ic() {
        return ic(true);
    }

    /**
     * 指定上个字段检索时是否忽略大小写
     * @param ignoreCase 是否忽略大小写
     * @return MapBuilder
     */
    @SuppressWarnings("unchecked")
    public B ic(boolean ignoreCase) {
        if (fieldParam == null) {
            throw new IllegalStateException("the method [ ic(...) ] must go after [ field(...) ] method");
        }
        fieldParam.setIgnoreCase(ignoreCase);
        return (B) this;
    }

//    @SuppressWarnings("unchecked")
//    public B and(Consumer<Builder<?>> condition) {
//        // TODO:
//        return (B) this;
//    }
//
//    @SuppressWarnings("unchecked")
//    public B or(Consumer<Builder<?>> condition) {
//        // TODO:
//        return (B) this;
//    }

    protected String toFieldName(FieldFn<?, ?> fieldFn) {
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

}
