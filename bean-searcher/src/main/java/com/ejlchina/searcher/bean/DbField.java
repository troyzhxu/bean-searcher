package com.ejlchina.searcher.bean;

import com.ejlchina.searcher.param.Operator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注解一个可检索 bean 的属性
 * 来指定属性对应数据库的哪张表的哪个字段，可与 @SearchBean 配合使用
 * 不可与 @DbIgnore 同时使用
 * @author Troy.Zhou @ 2017-03-20
 * @since v1.0.0
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DbField {

	/**
	 * 用于指定属性对应数据库的哪张表的哪个字段，例如:
	 * u.username
	 * 别名 u 由  @SearchBean 注解的 tables 值指定
	 * v3.0.0 后可空，为空时以字段名映射列名
	 * @return 数据库字段
	 * */
	String value() default "";

	/**
	 * @return 该字段是否可以被作为检索条件
	 * @since v3.0.0
	 */
	boolean conditional() default true;

	/**
	 * 用于指定该字段只允许接受的运算符，为空时，表示任意运算符都接受
	 * @return Operator[]
	 * @since v3.0.0
	 */
	Operator[] onlyOn() default {};

}

