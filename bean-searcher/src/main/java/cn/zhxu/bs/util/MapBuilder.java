package cn.zhxu.bs.util;

import cn.zhxu.bs.SearchException;
import cn.zhxu.bs.SearchParam;
import cn.zhxu.bs.param.FieldParam;
import cn.zhxu.bs.param.OrderBy;
import cn.zhxu.bs.param.Paging;
import cn.zhxu.bs.util.FieldFns.FieldFn;

import java.util.*;

/**
 * 检索参数构建器
 */
public class MapBuilder extends Builder<MapBuilder> {

    public static final String ORDER_BY = OrderBy.class.getName();
    public static final String PAGING = Paging.class.getName();
    // 因为存在非开放的自定义 SQL 运算符，所有这里加一个 UUID，杜绝前端 SQL 注入的可能
    public static final String FIELD_PARAM = FieldParam.class.getName() + UUID.randomUUID();
    public static final String ONLY_SELECT = SearchParam.class.getName() + ".ONLY_SELECT";
    public static final String SELECT_EXCLUDE = SearchParam.class.getName() + ".SELECT_EXCLUDE";
    public static final String GROUP_EXPR = SearchParam.class.getName() + ".GROUP_EXPR";

    public MapBuilder(Map<String, Object> map) {
        super(map);
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
     * 批量添加参数
     * @param params 参数集
     * @return MapBuilder
     * @since v3.8.1
     */
    public MapBuilder putAll(Map<String, ?> params) {
        map.putAll(params);
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
        return onlySelect(toFields(fieldFns));
    }

    /**
     * 指定只 Select 某些字段
     * @param fields 需要 Select 的字段名，可多个
     * @return MapBuilder
     */
    public MapBuilder onlySelect(String... fields) {
        return appendFields(obtainList(ONLY_SELECT), fields);
    }

    /**
     * 指定 Select 需要排除哪些字段
     * @param fieldFns 需要排除的字段表达式，可多个
     * @param <T> 泛型
     * @return MapBuilder
     */
    @SafeVarargs
    public final <T> MapBuilder selectExclude(FieldFn<T, ?>... fieldFns) {
        return selectExclude(toFields(fieldFns));
    }

    /**
     * 指定 Select 需要排除哪些字段
     * @param fields 需要排除的字段名，可多个
     * @return MapBuilder
     */
    public MapBuilder selectExclude(String... fields) {
        return appendFields(obtainList(SELECT_EXCLUDE), fields);
    }

    private MapBuilder appendFields(List<String> list, String... fields) {
        if (fields.length == 1) {
            Collections.addAll(list, fields[0].split(","));
        } else {
            Collections.addAll(list, fields);
        }
        return this;
    }

    /**
     * 开始一个分组（只对字段参数进行分组）
     * 在 {@link #field(String, Object...) } 方法之前使用 }
     * @since v3.5.0
     * @param group 组名（非空）
     * @return MapBuilder
     */
    public MapBuilder group(String group) {
        this.group = Objects.requireNonNull(group);
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
     * 指定按某个字段排序
     * v3.4.0 后支持调用多次，来指定多字段排序
     * @param <T> 泛型
     * @param fieldFn 字段表达式
     * @param order 排序方法：asc, desc
     * @return MapBuilder
     */
    public <T> MapBuilder orderBy(FieldFn<T, ?> fieldFn, String order) {
        return orderBy(FieldFns.name(fieldFn), order);
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
            List<OrderBy> orderBys = obtainList(ORDER_BY);
            Optional<OrderBy> orderByOpt = orderBys.stream()
                    .filter(orderBy -> fieldName.equals(orderBy.getSort()))
                    .findAny();
            if (orderByOpt.isPresent()) {
                throw new SearchException("Repeated sort field: " + fieldName + " " + order);
            } else {
                orderBys.add(new OrderBy(fieldName, order));
            }
        }
        return this;
    }

    /**
     * 指定按某个字段排序（默认升序）可再次调用 {@link #desc()} 或 {@link #asc()} 方法指定排序方法
     * @since v3.7.1
     * @param <T> 泛型
     * @param fieldFn 字段表达式
     * @return MapBuilder
     */
    public <T> MapBuilder orderBy(FieldFn<T, ?> fieldFn) {
        return orderBy(FieldFns.name(fieldFn), OrderBy.ORDER_ASC);
    }

    /**
     * 指定按某个字段排序（默认升序）可再次调用 {@link #desc()} 或 {@link #asc()} 方法指定排序方法
     * @since v3.7.1
     * @param fieldName 属性名
     * @return MapBuilder
     */
    public MapBuilder orderBy(String fieldName) {
        return orderBy(fieldName, OrderBy.ORDER_ASC);
    }

    /**
     * 升序，在 {@link #orderBy(FieldFn)} 方法之后调用
     * @since v3.7.1
     * @return MapBuilder
     */
    public MapBuilder asc() {
        List<OrderBy> orderBys = obtainList(ORDER_BY);
        if (orderBys.isEmpty()) {
            throw new IllegalStateException("The method asc(..) must go after orderBy(..) method.");
        }
        int index = orderBys.size() - 1;
        OrderBy last = orderBys.get(index);
        if (last.isDesc()) {
            orderBys.set(index, last.asc());
        }
        return this;
    }

    /**
     * @since v3.8.1
     * @param sure 是否确定升序
     * @return MapBuilder
     */
    public MapBuilder asc(boolean sure) {
        if (sure) {
            return asc();
        }
        return this;
    }

    /**
     * 降序，在 {@link #orderBy(FieldFn)} 方法之后调用
     * @since v3.7.1
     * @return MapBuilder
     */
    public MapBuilder desc() {
        List<OrderBy> orderBys = obtainList(ORDER_BY);
        if (orderBys.isEmpty()) {
            throw new IllegalStateException("The method desc(..) must go after orderBy(..) method.");
        }
        int index = orderBys.size() - 1;
        OrderBy last = orderBys.get(index);
        if (last.isAsc()) {
            orderBys.set(index, last.desc());
        }
        return this;
    }

    /**
     * @since v3.8.1
     * @param sure 是否确定降序
     * @return MapBuilder
     */
    public MapBuilder desc(boolean sure) {
        if (sure) {
            return desc();
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
        map.put(PAGING, new Page(size, page));
        return this;
    }

    /**
     * 分页
     * @param offset 偏移量，从 0 开始
     * @param size 每页大小
     * @return MapBuilder
     */
    public MapBuilder limit(long offset, int size) {
        map.put(PAGING, new Limit(size, offset));
        return this;
    }

    /**
     * 构建参数
     * @return 检索参数
     */
    public Map<String, Object> build() {
        return map;
    }

    public static class Page {

        private final int size;
        private final long page;

        public Page(int size, long page) {
            this.size = size;
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public long getPage() {
            return page;
        }

    }

    public static class Limit {

        private final int size;
        private final long offset;

        public Limit(int size, long offset) {
            this.size = size;
            this.offset = offset;
        }

        public int getSize() {
            return size;
        }

        public long getOffset() {
            return offset;
        }

    }

}
