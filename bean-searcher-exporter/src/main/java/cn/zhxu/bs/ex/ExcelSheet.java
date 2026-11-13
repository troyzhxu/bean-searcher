package cn.zhxu.bs.ex;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于指定导出的是 Excel 文件
 * @author Troy.Zhou @ 2026-11-13
 * @since v5.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ExcelSheet {

    /**
     * Excel Sheet 名称
     */
    String name() default "数据";

    /**
     * Excel 行高，默认 0 表示不指定行高
     */
    int rowHeight() default 0;

    /**
     * Excel 表头是否加粗
     */
    boolean headerBold() default true;

    /**
     * 是否冻结表头
     */
    boolean freezeHeader() default false;

}
