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
     * 值转换表达式，用与在导出前将字段值进行变换，可直接使用 @ 符引用当前字段的值，使用字段名引用其它字段的值 <br>
     * 在 Spring 环境下支持 SpEL 表达式语法 <br>
     * 在 Solon 环境下支持 SnEL 表达式语法 <br>
     * 你也可以通过自定义 {@link Expresser } 来重新定义表达式的解析规则
     * @return 值转换表达式
     */
    String expr() default "";

    /**
     * 格式化输出，支持 日期 / 数字 / 字符串 三种类型的格式 <br>
     * 如果同时配置了值转换表达式 {@link #expr()}, 则先进行表达式值转换，然后再对表达式的计算结果进行格式化 <br>
     * 你也可以通过自定义 {@link Formatter } 来重新定义格式化的规则
     * @return 格式
     */
    String format() default "";

    /**
     * 字段导出条件，可以使用参数名引用检索参数（paraMap）中的值
     * 在 Spring 环境下支持 SpEL 表达式语法 <br>
     * 在 Solon 环境下支持 SnEL 表达式语法 <br>
     * @return 仅当条件满足时才导出，默认为 true
     * @since v4.8.0
     */
    String onlyIf() default "";

}
