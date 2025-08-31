package cn.zhxu.bs.ex;

import java.lang.annotation.*;

/**
 * 字段导出注解
 * @author Troy.Zhou @ 2025/8/28
 * @since v4.5.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface Export {

    /**
     * @return 列名
     */
    String name();

    /**
     * @return 列顺序（小在前，大在后）
     */
    int idx() default 0;

    /**
     * 支持 SpEL 表达式（Spring 环境下）或 SnEL 表达式（Solon 环境下），使用 @ 符引用当前字段的值，使用字段名引用其它字段的值
     * @return 值转换表达式
     */
    String expr() default "";

    /**
     * 支持 日期 / 数字 / 字符串 格式
     * @return 格式
     */
    String format() default "";

}
