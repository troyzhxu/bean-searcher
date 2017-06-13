package com.ejl.searcher.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Troy.Zhou @ 2017-03-20
 *
 * 用于注解一个可检索 bean 的 class
 * 
 */

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SearchBean {


	/**
	 * 参与检索的数据库表名，例如:
	 * users u, user_role ur, roles r
	 * @return tables
	 */
	String tables();
	
	/**
	 * 参与检索的数据表的连接条件，例如：
	 * u.id = ur.user_id and ur.role_id = r.id 
	 * @return join condition
	 * */
	String joinCond() default "";	
	
	/**
	 * 分组字段，例如
	 * u.id,r.name
	 * @return group infomation
	 */
	String groupBy() default "";
	
	/**
	 * 是否 distinct 结果
	 * @return distinct
	 * */
	boolean distinct() default false;
	
}

