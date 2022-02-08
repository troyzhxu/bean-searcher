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
     * 排序字段（用于排序）
     */
    private String sort;

    /**
     * 排序方法：desc, asc（用于排序）
     */
    private String order;

    public OrderBy(String sort, String order) {
        this.sort = sort;
        this.order = order;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public boolean isValid(Set<String> fieldSet) {
        return fieldSet.contains(sort) && (StringUtils.isBlank(order) || ORDER_ASC.equalsIgnoreCase(order) || ORDER_DESC.equalsIgnoreCase(order));
    }

}
