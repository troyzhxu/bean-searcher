package com.ejlchina.searcher.param;

/**
 * 分页参数
 * @author Troy.Zhou @ 2021-10-31
 * @since v3.0.0
 */
public class LimitParam {

    /**
     * 分页：最大条数（用于分页）
     */
    private int size;

    /**
     * 分页：查询偏移条数（用于分页）
     */
    private long offset;

    public LimitParam(int size, long offset) {
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

}
