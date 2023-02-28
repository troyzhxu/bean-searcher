package cn.zhxu.bs.bean;

/**
 * 用于标记某字段是否是聚合字段
 * @since v4.1.0
 * @author Troy.Zhou @ 2023-02-28
 */
public enum Cluster {

    /**
     * 是聚合字段
     */
    TRUE,

    /**
     * 不是聚合字段
     */
    FALSE,

    /**
     * 自动推断：<p>
     * 当使用 groupBy 并且该字段未在 groupBy 列表中时，自动推断为 {@link #CLUSTER }，其它情况都是 {@link #RAW }，
     */
    AUTO,

}
