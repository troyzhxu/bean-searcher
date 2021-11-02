package com.ejlchina.searcher.bean;

import java.lang.annotation.*;

/**
 * 用于注解一个可检索 bean 的属性
 * 被该注解标记的属性，将不作数据库表字段自动映射
 * 不可与 @DbField 同时使用
 * @author Troy.Zhou @ 2021-11-02
 * @since v3.0.0
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DbIgnore { }
