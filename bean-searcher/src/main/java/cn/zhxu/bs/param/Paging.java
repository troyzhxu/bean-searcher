package cn.zhxu.bs.param;

import cn.zhxu.bs.implement.DefaultSqlResolver;

/**
 * 分页参数
 * @author Troy.Zhou @ 2021-10-31
 * @since v3.0.0
 */
public class Paging {

    /**
     * 分页：最大条数（用于分页）
     */
    private int size;

    /**
     * 分页：查询偏移条数（用于分页）
     */
    private long offset;

    /**
     * 是否有排序字段（内部字段，由 {@link DefaultSqlResolver } 自动管理）
     * @since v4.8.3
     */
    private boolean ordering;

    public Paging(int size, long offset) {
        this.size = size;
        this.offset = offset;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public boolean isOrdering() {
        return ordering;
    }

    public void setOrdering(boolean ordering) {
        this.ordering = ordering;
    }

}
