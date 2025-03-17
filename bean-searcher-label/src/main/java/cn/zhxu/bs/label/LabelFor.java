package cn.zhxu.bs.label;

import cn.zhxu.bs.bean.DbIgnore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标注一个 Label 属性
 * @author Troy.Zhou @ 2021-11-02
 * @since v4.4.0
 */
@DbIgnore
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface LabelFor {

    /**
     * 用于标记该属性所引用的字段名
     * @return 引用字段名
     */
    String value();

    /**
     * 用于指定 Label 的类型，若不指定，则默认取被标注的字段名
     * @return 键值
     */
    String key() default "";

}
