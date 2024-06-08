package cn.zhxu.bs;

import java.util.Objects;

/**
 * 字段名称配置
 * @since v4.3
 */
public class ParamNames<T extends ParamNames<T>> {

    /**
     * 排序字段参数名
     */
    protected String sort = "sort";

    /**
     * 排序方法字段参数名
     */
    protected String order = "order";

    /**
     * 排序参数名（该参数与 {@link #sort } 和 {@link #order } 指定的参数互斥）
     * 该参数可指定多个排序字段，例如：orderBy=age:desc,dateCreate:asc
     */
    protected String orderBy = "orderBy";

    /**
     * 参数名分割符
     */
    protected String separator = "-";

    /**
     * 忽略大小写参数名后缀
     * 带上该参数会导致字段索引不被使用，查询速度降低，在大数据量下建议使用数据库本身的字符集实现忽略大小写功能。
     */
    protected String ic = "ic";

    /**
     * 过滤运算符参数名后缀
     */
    protected String op = "op";

    /**
     * 用于指定只 Select 某些字段的参数名
     */
    protected String onlySelect = "onlySelect";

    /**
     * 用于指定不需要 Select 的字段的参数名
     */
    protected String selectExclude = "selectExclude";

    /**
     * 用于指定组表达式参数名
     */
    protected String gexpr = "gexpr";

    /**
     * 组分割符
     */
    protected String groupSeparator = ".";

    public String sort() {
        return sort;
    }

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    public T sort(String sort) {
        this.sort = Objects.requireNonNull(sort);
        return self();
    }

    public String order() {
        return order;
    }

    public T order(String order) {
        this.order = Objects.requireNonNull(order);
        return self();
    }

    public String orderBy() {
        return orderBy;
    }

    public T orderBy(String orderBy) {
        this.orderBy = Objects.requireNonNull(orderBy);
        return self();
    }

    public String separator() {
        return separator;
    }

    public T separator(String separator) {
        this.separator = Objects.requireNonNull(separator);
        return self();
    }

    public String ic() {
        return ic;
    }

    public T ic(String ic) {
        this.ic = Objects.requireNonNull(ic);
        return self();
    }

    public String op() {
        return op;
    }

    public T op(String op) {
        this.op = Objects.requireNonNull(op);
        return self();
    }

    public String onlySelect() {
        return onlySelect;
    }

    public T onlySelect(String onlySelect) {
        this.onlySelect = Objects.requireNonNull(onlySelect);
        return self();
    }

    public String selectExclude() {
        return selectExclude;
    }

    public T selectExclude(String selectExclude) {
        this.selectExclude = Objects.requireNonNull(selectExclude);
        return self();
    }

    public String gexpr() {
        return gexpr;
    }

    public T gexpr(String gexpr) {
        this.gexpr = Objects.requireNonNull(gexpr);
        return self();
    }

    public String groupSeparator() {
        return groupSeparator;
    }

    public T groupSeparator(String groupSeparator) {
        this.groupSeparator = Objects.requireNonNull(groupSeparator);
        return self();
    }

}
