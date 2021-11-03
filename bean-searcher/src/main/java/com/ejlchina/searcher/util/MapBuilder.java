package com.ejlchina.searcher.util;

import com.ejlchina.searcher.param.FieldParam;
import com.ejlchina.searcher.param.Operator;
import com.ejlchina.searcher.param.OrderBy;
import com.ejlchina.searcher.param.Paging;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MapBuilder {

    @FunctionalInterface
    public interface FieldFn<T, R> extends Function<T, R>, Serializable {  }

    private final Map<FieldFn<?, ?>, String> cache = new ConcurrentHashMap<>();

    private final Map<String, Object> map;

    private FieldParam lastFieldParam = null;

    public MapBuilder(Map<String, Object> map) {
        this.map = map;
    }

    public MapBuilder put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public <T> MapBuilder field(FieldFn<T, ?> fieldFn, Object... values) {
        return field(toFieldName(fieldFn), values);
    }

    public <T> MapBuilder field(String fieldName, Object... values) {
        List<FieldParam.Value> pValues = new ArrayList<>();
        for (int index = 0; index < values.length; index++) {
            pValues.add(new FieldParam.Value(values[index], index));
        }
        lastFieldParam = new FieldParam(fieldName, pValues);
        @SuppressWarnings("unchecked")
        List<FieldParam> params = (List<FieldParam>) map.get(FieldParam.class.getName());
        if (params == null) {
            params = new ArrayList<>();
            map.put(FieldParam.class.getName(), params);
        }
        params.add(lastFieldParam);
        return this;
    }

    public <T> MapBuilder op(String operator) {
        return op(Operator.from(operator));
    }

    public <T> MapBuilder op(Operator operator) {
        if (lastFieldParam == null) {
            throw new IllegalStateException("the method [ op(...) ] must go after [ field(...) ] method");
        }
        lastFieldParam.setOperator(operator);
        return this;
    }

    public <T> MapBuilder ic() {
        return ic(true);
    }

    public <T> MapBuilder ic(boolean ignoreCase) {
        if (lastFieldParam == null) {
            throw new IllegalStateException("the method [ ic(...) ] must go after [ field(...) ] method");
        }
        lastFieldParam.setIgnoreCase(ignoreCase);
        return this;
    }

    public <T> MapBuilder orderBy(FieldFn<T, ?> fieldFn, String order) {
        return orderBy(toFieldName(fieldFn), order);
    }

    public <T> MapBuilder orderBy(String fieldName, String order) {
        map.put(OrderBy.class.getName(), new OrderBy(fieldName, order));
        return this;
    }

    public <T> MapBuilder page(long page, int size) {
        return limit(page * size, size);
    }

    public <T> MapBuilder limit(long offset, int size) {
        map.put(Paging.class.getName(), new Paging(size, offset));
        return this;
    }

    private String toFieldName(FieldFn<?, ?> fieldFn) {
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
