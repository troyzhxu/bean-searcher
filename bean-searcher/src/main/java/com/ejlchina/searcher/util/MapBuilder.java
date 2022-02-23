package com.ejlchina.searcher.util;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.SearchException;
import com.ejlchina.searcher.SearchParam;
import com.ejlchina.searcher.param.FieldParam;
import com.ejlchina.searcher.param.OrderBy;
import com.ejlchina.searcher.param.Paging;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 检索参数构建起
 */
public class MapBuilder {

    public static final String ORDER_BY = OrderBy.class.getName();
    public static final String PAGING = Paging.class.getName();
    public static final String FIELD_PARAM = FieldParam.class.getName();
    public static final String ONLY_SELECT = SearchParam.class.getName() + ".ONLY_SELECT";
    public static final String SELECT_EXCLUDE = SearchParam.class.getName() + ".SELECT_EXCLUDE";
    public static final String GROUP_EXPR = SearchParam.class.getName() + ".GROUP_EXPR";

    @FunctionalInterface
    public interface FieldFn<T, R> extends Function<T, R>, Serializable {  }

    private final Map<FieldFn<?, ?>, String> cache = new ConcurrentHashMap<>();

    private final Map<String, Object> map;

    private FieldParam fieldParam = null;

    private String group = null;

    public MapBuilder(Map<String, Object> map) {
        this.map = map;
    }

    /**
     * 添加参数
     * @param key 参数名
     * @param value 参数值
     * @return MapBuilder
     */
    public MapBuilder put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    /**
     * 指定只 Select 某些字段
     * @param fieldFns 需要 Select 的字段表达式，可多个
     * @param <T> 泛型
     * @return MapBuilder
     */
    @SafeVarargs
    public final <T> MapBuilder onlySelect(FieldFn<T, ?>... fieldFns) {
        String[] fields = new String[fieldFns.length];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = toFieldName(fieldFns[i]);
        }
        return onlySelect(fields);
    }

    /**
     * 指定只 Select 某些字段
     * @param fields 需要 Select 的字段名，可多个
     * @return MapBuilder
     */
    public MapBuilder onlySelect(String... fields) {
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) map.get(ONLY_SELECT);
        if (list == null) {
            list = new ArrayList<>();
            map.put(ONLY_SELECT, list);
        }
        Collections.addAll(list, fields);
        return this;
    }

    /**
     * 指定 Select 需要排除哪些字段
     * @param fieldFns 需要排除的字段表达式，可多个
     * @param <T> 泛型
     * @return MapBuilder
     */
    @SafeVarargs
    public final <T> MapBuilder selectExclude(FieldFn<T, ?>... fieldFns) {
        String[] fields = new String[fieldFns.length];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = toFieldName(fieldFns[i]);
        }
        return selectExclude(fields);
    }

    /**
     * 指定 Select 需要排除哪些字段
     * @param fields 需要排除的字段名，可多个
     * @return MapBuilder
     */
    public MapBuilder selectExclude(String... fields) {
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) map.get(SELECT_EXCLUDE);
        if (list == null) {
            list = new ArrayList<>();
            map.put(SELECT_EXCLUDE, list);
        }
        Collections.addAll(list, fields);
        return this;
    }

    /**
     * 开始一个分组（只对字段参数进行分组）
     * 在 {@link #field(String, Object...) } 方法之前使用 }
     * @since v3.5.0
     * @param group 组名
     * @return MapBuilder
     */
    public MapBuilder group(String group) {
        this.group = group;
        return this;
    }

    /**
     * 设置组表达式（用于表达参数组之间的逻辑关系）
     * @since v3.5.0
     * @param gExpr 表达式
     * @return MapBuilder
     */
    public MapBuilder groupExpr(String gExpr) {
        map.put(GROUP_EXPR, gExpr);
        return this;
    }

    /**
     * 指定某个字段的检索值
     * @param fieldFn 字段表达式
     * @param values 检索值，可多个
     * @param <T> 泛型
     * @return MapBuilder
     */
    public <T> MapBuilder field(FieldFn<T, ?> fieldFn, Object... values) {
        return field(toFieldName(fieldFn), values);
    }

    /**
     * 指定某个字段的检索值
     * @param fieldName 字段名
     * @param values 检索值，可多个
     * @return MapBuilder
     */
    public MapBuilder field(String fieldName, Object... values) {
        if (fieldName != null) {
            List<FieldParam.Value> pValues = new ArrayList<>();
            for (int index = 0; index < values.length; index++) {
                pValues.add(new FieldParam.Value(values[index], index));
            }
            String field = fieldName.trim();
            fieldParam = new FieldParam(field, pValues);
            if (group != null) {
                map.put(group + FIELD_PARAM + field, fieldParam);
            } else {
                map.put(FIELD_PARAM + field, fieldParam);
            }
        }
        return this;
    }

    /**
     * 指定上个字段的运算符
     * @param operator 检索运算符
     * @return MapBuilder
     */
    public MapBuilder op(String operator) {
        return fieldOp(operator);
    }

    /**
     * 指定上个字段的运算符
     * @param operator 检索运算符
     * @return MapBuilder
     */
    public MapBuilder op(Class<? extends FieldOp> operator) {
        return fieldOp(operator);
    }

    /**
     * 指定上个字段的运算符
     * @param operator 检索运算符
     * @return MapBuilder
     */
    public MapBuilder op(FieldOp operator) {
        return fieldOp(operator);
    }

    private MapBuilder fieldOp(Object operator) {
        if (fieldParam == null) {
            throw new IllegalStateException("the method [ op(...) ] must go after [ field(...) ] method");
        }
        fieldParam.setOperator(operator);
        return this;
    }

    /**
     * 指定上个字段检索时忽略大小写
     * @return MapBuilder
     */
    public MapBuilder ic() {
        return ic(true);
    }

    /**
     * 指定上个字段检索时是否忽略大小写
     * @param ignoreCase 是否忽略大小写
     * @return MapBuilder
     */
    public MapBuilder ic(boolean ignoreCase) {
        if (fieldParam == null) {
            throw new IllegalStateException("the method [ ic(...) ] must go after [ field(...) ] method");
        }
        fieldParam.setIgnoreCase(ignoreCase);
        return this;
    }

    /**
     * 指定按某个字段排序
     * v3.4.0 后支持调用多次，来指定多字段排序
     * @param <T> 泛型
     * @param fieldFn 字段表达式
     * @param order 排序方法：asc, desc
     * @return MapBuilder
     */
    public <T> MapBuilder orderBy(FieldFn<T, ?> fieldFn, String order) {
        return orderBy(toFieldName(fieldFn), order);
    }

    /**
     * 指定按某个字段排序
     * v3.4.0 后支持调用多次，来指定多字段排序
     * @param fieldName 属性名
     * @param order 排序方法：asc, desc
     * @return MapBuilder
     */
    public MapBuilder orderBy(String fieldName, String order) {
        if (fieldName != null) {
            @SuppressWarnings("unchecked")
            List<OrderBy> orderBys = (List<OrderBy>) map.get(ORDER_BY);
            if (orderBys == null) {
                orderBys = new ArrayList<>();
                map.put(ORDER_BY, orderBys);
            }
            Optional<OrderBy> orderByOpt = orderBys.stream()
                    .filter(orderBy -> fieldName.equals(orderBy.getSort()))
                    .findAny();
            if (orderByOpt.isPresent()) {
                throw new SearchException("重复添加排序字段：" + fieldName + " " + order);
            } else {
                orderBys.add(new OrderBy(fieldName, order));
            }
        }
        return this;
    }

    /**
     * 分页
     * @param page 页码，从 0 开始
     * @param size 每页大小
     * @return MapBuilder
     */
    public MapBuilder page(long page, int size) {
        return limit(page * size, size);
    }

    /**
     * 分页
     * @param offset 偏移量，从 0 开始
     * @param size 每页大小
     * @return MapBuilder
     */
    public MapBuilder limit(long offset, int size) {
        map.put(PAGING, new Paging(size, offset));
        return this;
    }

    /**
     * 构建参数
     * @return 检索参数
     */
    public Map<String, Object> build() {
        return map;
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

}
