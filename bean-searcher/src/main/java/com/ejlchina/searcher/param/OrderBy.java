package com.ejlchina.searcher.param;

/**
 * 排序参数
 * @author Troy.Zhou @ 2021-10-31
 * @since v3.0.0
 */
public class OrderBy {

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

}
