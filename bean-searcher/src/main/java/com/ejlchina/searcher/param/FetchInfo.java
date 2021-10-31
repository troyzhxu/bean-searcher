package com.ejlchina.searcher.param;

/**
 * Fetch 信息
 * @author Troy.Zhou @ 2021-10-31
 * @since v3.0.0
 */
public class FetchInfo {

    /**
     * 需要求和的字段
     */
    private final String[] summaryFields;

    /**
     * 需要查询总数
     */
    private final boolean shouldQueryTotal;

    /**
     * 需要查询列表
     */
    private final boolean shouldQueryList;

    /**
     * 是否查询所有数据，即不分页
     */
    private final boolean fetchAll;


    public FetchInfo(String[] summaryFields, boolean shouldQueryTotal, boolean shouldQueryList, boolean fetchAll) {
        if (summaryFields == null) {
            this.summaryFields = new String[] { };
        } else {
            this.summaryFields = summaryFields;
        }
        this.shouldQueryTotal = shouldQueryTotal;
        this.shouldQueryList = shouldQueryList;
        this.fetchAll = fetchAll;
    }

    public String[] getSummaryFields() {
        return summaryFields;
    }

    public boolean isShouldQueryTotal() {
        return shouldQueryTotal;
    }

    public boolean isShouldQueryList() {
        return shouldQueryList;
    }

    public boolean isFetchAll() {
        return fetchAll;
    }

}
