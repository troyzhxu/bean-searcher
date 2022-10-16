package cn.zhxu.bs.bean;

/**
 * 排序约束类型
 * @author Troy.Zhou @ 2020-04-18
 * @since v3.6.0
 */
public enum SortType {

    /**
     * 默认，根据检索器的配置
     */
    DEFAULT,

    /**
     * 只允许在实体类内通过 {@link SearchBean } 注解指定排序字段
     */
    ONLY_ENTITY,

    /**
     * 允许在检索参数中指定排序字段
     */
    ALLOW_PARAM

}
