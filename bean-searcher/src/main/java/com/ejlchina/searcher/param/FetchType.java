package com.ejlchina.searcher.param;

/**
 * Fetch 信息
 * @author Troy.Zhou @ 2021-10-31
 * @since v3.0.0
 */
public class FetchType {

    /**
     * 列表、总条数与统计都查询
     */
    public static final int ALL = 0;
    /**
     * 只查询第一条列表数据
     */
    public static final int LIST_FIRST = 1;
    /**
     * 只查询列表数据（分页）
     */
    public static final int LIST_ONLY = 2;
    /**
     * 查询所有列表数据，即不分页
     */
    public static final int LIST_ALL = 3;
    /**
     * 只查询总条数
     */
    public static final int ONLY_TOTAL = 4;
    /**
     * 只查询统计信息
     */
    public static final int ONLY_SUMMARY = 5;


    private static final String[] EMPTY_FIELDS = {};

    /**
     * Fetch 类型
     *   @see #ALL
     *   @see #LIST_FIRST
     *   @see #LIST_ONLY
     *   @see #LIST_ALL
     *   @see #ONLY_TOTAL
     *   @see #ONLY_SUMMARY
     **/
    private final int fetchType;

    /**
     * 需要求和的字段
     */
    private final String[] summaryFields;


    public FetchType(int fetchType) {
        this(fetchType, null);
    }

    public FetchType(int fetchType, String[] summaryFields) {
        if (summaryFields == null) {
            this.summaryFields = EMPTY_FIELDS;
        } else {
            this.summaryFields = summaryFields;
        }
        this.fetchType = fetchType;
    }

    public int getFetchType() {
        return fetchType;
    }

    public String[] getSummaryFields() {
        return summaryFields;
    }

    public boolean isShouldQueryTotal() {
        return fetchType == ALL || fetchType == ONLY_TOTAL;
    }

    public boolean isShouldQueryCluster() {
        return isShouldQueryTotal() || summaryFields.length > 0;
    }

    public boolean isShouldQueryList() {
        return fetchType != ONLY_TOTAL && fetchType != ONLY_SUMMARY;
    }

    public boolean isFetchAll() {
        return fetchType == LIST_ALL;
    }

}
