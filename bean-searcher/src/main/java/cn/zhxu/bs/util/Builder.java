package cn.zhxu.bs.util;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.operator.SqlCond;
import cn.zhxu.bs.param.FieldParam;
import cn.zhxu.bs.util.FieldFns.FieldFn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.zhxu.bs.util.MapBuilder.FIELD_PARAM;


/**
 * 检索参数构建器基类
 * @param <B> 泛型
 */
public class Builder<B extends Builder<B>> {

    /**
     * 根组，根组的条件总是会被用到
     */
    public static final String ROOT_GROUP = "$";

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
        return field(FieldFns.name(fieldFn), values);
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
     * 指定某个（多个）字段，一般配和 {@link #sql(String)} 一起使用，例如
     * <pre>{@code
     * Map<String, Object> params = MapUtils.builder()
     *     // 生成 SQL 条件：username = nickname or username = 'Jack'
     *     .field(User::getUserName, User::getNickName).sql("$1 = $2 or $1 = 'Jack'")
     *     .build();
     * }</pre>
     * @param fieldFn 字段方法引用
     * @param fieldFns 字段方法引用
     * @param <T> 泛型
     * @return MapBuilder
     * @since 3.8.0
     */
    @SafeVarargs
    public final <T> B field(FieldFn<T, ?> fieldFn, FieldFn<T, ?>... fieldFns) {
        return field(FieldFns.name(fieldFn), (Object[]) toFields(fieldFns));
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
            throw new IllegalStateException("The method op(...) must go after field(...) method.");
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
            throw new IllegalStateException("The method ic(...) must go after field(...) method.");
        }
        fieldParam.setIgnoreCase(ignoreCase);
        return (B) this;
    }

    /**
     * 自定义 SQL 条件，一般配和 {@link #field(FieldFn, FieldFn[])} 一起使用，例如：
     * <pre>{@code
     * Map<String, Object> params = MapUtils.builder()
     *     // 生成 SQL 条件：username = nickname or username = 'Jack'
     *     .field(User::getUserName, User::getNickName).sql("$1 = $2 or $1 = 'Jack'")
     *     .build();
     * }</pre>
     * @param sqlCond Sql 条件片段（支持占位符：$n 表示方法 field(..) 中指定的第 n 个字段）
     * @return MapBuilder
     * @since v3.8.0
     */
    public B sql(String sqlCond) {
        return fieldOp(new SqlCond(sqlCond));
    }

    /**
     * 自定义 SQL 条件，一般配和 {@link #field(FieldFn, FieldFn[])} 一起使用，例如：
     * <pre>{@code
     * Map<String, Object> params = MapUtils.builder()
     *     // 生成 SQL 条件：id < ? or age > ?，两个占位符参数分别为：100，20
     *     .field(User::getId, User::getAge).sql("$1 < ? or $2 > ?", 100, 20)
     *     .build();
     * }</pre>
     * @param sqlCond Sql 条件片段（支持占位符：$n 表示方法 field(..) 中指定的第 n 个字段）
     * @param args Sql 参数，对应 sqlCond 里的 '?' 占位符
     * @return MapBuilder
     * @since v3.8.0
     */
    public B sql(String sqlCond, Object... args) {
        return fieldOp(new SqlCond(sqlCond, args));
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

    /**
     * Deprecated from v3.8.1
     * Please use {@link FieldFns#name(FieldFn)} directly
     * @param fieldFn 方法引用
     * @return 属性名
     */
    @Deprecated
    protected String toFieldName(FieldFn<?, ?> fieldFn) {
        return FieldFns.name(fieldFn);
    }

    @SafeVarargs
    protected final <T> String[] toFields(FieldFn<T, ?>... fieldFns) {
        String[] fields = new String[fieldFns.length];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = FieldFns.name(fieldFns[i]);
        }
        return fields;
    }

    protected <T> List<T> obtainList(String key) {
        Object value = map.get(key);
        List<T> list = null;
        if (value instanceof List) {
            @SuppressWarnings("all")
            List<T> l = (List<T>) value;
            list = l;
        }
        if (list == null) {
            list = new ArrayList<>();
            map.put(key, list);
        }
        return list;
    }

}
