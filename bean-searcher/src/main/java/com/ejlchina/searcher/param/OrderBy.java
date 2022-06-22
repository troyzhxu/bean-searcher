package com.ejlchina.searcher.param;

import com.ejlchina.searcher.util.StringUtils;

import java.util.Set;

/**
 * 排序参数
 * @author Troy.Zhou @ 2021-10-31
 * @since v3.0.0
 */
public class OrderBy {

    public static final String ORDER_ASC = "asc";
    public static final String ORDER_DESC = "desc";

    /**
     * 排序字段
     */
    private final String sort;

    /**
     * 排序方法：desc 或 asc
     */
    private final String order;

    public OrderBy(String sort, String order) {
        this.sort = sort;
        this.order = order;
    }

    public String getSort() {
        return sort;
    }

    public String getOrder() {
        return order;
    }

    public boolean isValid(Set<String> fieldSet) {
        return fieldSet.contains(sort) && (StringUtils.isBlank(order) || ORDER_ASC.equalsIgnoreCase(order) || ORDER_DESC.equalsIgnoreCase(order));
    }

    public boolean isAsc() {
        return ORDER_ASC.equalsIgnoreCase(order);
    }

    public boolean isDesc() {
        return ORDER_DESC.equalsIgnoreCase(order);
    }

    public OrderBy asc() {
        return new OrderBy(sort, ORDER_ASC);
    }

    public OrderBy desc() {
        return new OrderBy(sort, ORDER_DESC);
    }

}
