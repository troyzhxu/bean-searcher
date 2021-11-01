package com.ejlchina.searcher.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注解一个可检索 Bean
 * @author Troy.Zhou @ 2017-03-20
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SearchBean {

	/**
	 * 参与检索的数据库表名，例如:
	 * users u, user_role ur, roles r
	 * v3.0.0 后可以不给值，没有值的时候以类名做表名
	 * @return tables
	 */
	String tables() default "";
	
	/**
	 * 参与检索的数据表的连接条件，例如：
	 * u.id = ur.user_id and ur.role_id = r.id 
	 * @return join condition
	 * */
	String joinCond() default "";	
	
	/**
	 * 分组字段，例如
	 * u.id,r.name
	 * @return group information
	 */
	String groupBy() default "";
	
	/**
	 * 是否 distinct 结果
	 * @return distinct
	 * */
	boolean distinct() default false;

	/**
	 * 字段未加 @DbField 时，指定它映射到那张表（只有在 tables 属性不空时起作用）
	 * 如果 autoMapping 为空，则表示未被 @DbField 注解的字段不需要映射
	 * @return 自动映射的表名 或 别名
	 */
	String autoMapTab() default "";

}

