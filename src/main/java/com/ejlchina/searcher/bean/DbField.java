package com.ejlchina.searcher.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 用于注解一个可检索 bean 的属性
 * 来指定属性对应数据库的哪张表的哪个字段，与 @SearchBean 配合使用
 * 
 * @author Troy.Zhou @ 2017-03-20
 *  
 */

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DbField {

	/**
	 * 用于指定属性对应数据库的哪张表的哪个字段，例如:
	 * u.username
	 * 别名 u 由  @SearchBean 注解的 tables 值指定 
	 * @return 数据库字段
	 * */
	String value();

	
}

