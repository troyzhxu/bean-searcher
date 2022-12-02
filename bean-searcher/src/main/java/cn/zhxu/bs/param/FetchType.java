package cn.zhxu.bs.param;

/**
 * Fetch 信息
 * @author Troy.Zhou @ 2021-10-31
 * @since v3.0.0
 */
public class FetchType {

    /**
     * 列表、总条数与统计都查询
     */
    public static final int DEFAULT = 0;

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

    /**
     * 空字段
     */
    private static final String[] EMPTY_FIELDS = {};

    /**
     * Fetch 类型
     *   @see #DEFAULT
     *   @see #LIST_FIRST
     *   @see #LIST_ONLY
     *   @see #LIST_ALL
     *   @see #ONLY_TOTAL
     *   @see #ONLY_SUMMARY
     **/
    private final int type;

    /**
     * 需要求和的字段
     */
    private final String[] summaryFields;


    public FetchType(int type) {
        this(type, null);
    }

    public FetchType(int type, String[] summaryFields) {
        if (summaryFields == null) {
            this.summaryFields = EMPTY_FIELDS;
        } else {
            this.summaryFields = summaryFields;
        }
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String[] getSummaryFields() {
        return summaryFields;
    }

    public boolean shouldQueryTotal() {
        return type == DEFAULT || type == ONLY_TOTAL;
    }

    public boolean shouldQueryCluster() {
        return shouldQueryTotal() || summaryFields.length > 0;
    }

    public boolean shouldQueryList() {
        return type != ONLY_TOTAL && type != ONLY_SUMMARY;
    }

    /**
     * @return 是否可以分页
     */
    public boolean canPaging() {
        return type != LIST_ALL && type != ONLY_TOTAL && type != ONLY_SUMMARY;
    }

    /**
     * @return 是否只查询一条数据
     */
    public boolean isFetchFirst() {
        return type == LIST_FIRST;
    }

}
