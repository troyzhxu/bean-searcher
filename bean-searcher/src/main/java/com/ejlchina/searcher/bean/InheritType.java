package com.ejlchina.searcher.bean;

import com.ejlchina.searcher.DbMapping;

/**
 * 继承类型
 * 表示 SearchBean 与父类之间的表映射继承关系
 * @author Troy.Zhou @ 2017-03-21
 * @since v3.2.0
 */
public enum InheritType {

    /**
     * 使用全局默认值 {@link DbMapping#getInheritType() }
     */
    DEFAULT,

    /**
     * 不继承
     */
    NONE,

    /**
     * 表继承
     */
    TABLE,

    /**
     * 字段继承
     */
    FIELD,

    /**
     * 表 与 字段 都继承
     */
    ALL

}
