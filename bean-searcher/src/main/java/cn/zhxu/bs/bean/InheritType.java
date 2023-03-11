package cn.zhxu.bs.bean;

import cn.zhxu.bs.DbMapping;

/**
 * 继承类型
 * 表示 SearchBean 与父类之间的表映射继承关系
 * @author Troy.Zhou @ 2017-03-21
 * @since v3.2.0
 */
public enum InheritType {

    /**
     * 使用全局默认值 {@link DbMapping#inheritType(Class)}  }
     */
    DEFAULT,

    /**
     * 不继承
     */
    NONE,

    /**
     * 继承 {@link SearchBean} 注解
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
