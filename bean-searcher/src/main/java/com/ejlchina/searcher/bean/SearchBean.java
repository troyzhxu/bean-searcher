package com.ejlchina.searcher.bean;

import com.ejlchina.searcher.DbMapping;
import com.ejlchina.searcher.implement.DefaultDbMapping;
import com.ejlchina.searcher.implement.DefaultSqlExecutor;

import javax.sql.DataSource;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注解一个 SearchBean
 * v3.0.0 后该注解可以缺省，缺省时根据 {@link DbMapping } 自动映射数据库表
 * @author Troy.Zhou @ 2017-03-20
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SearchBean {

	/**
	 * 指定数据源
	 * @see DefaultSqlExecutor#setDataSource(String name, DataSource)
	 * @since v3.0.0
	 * @return 数据源名称（name of DataSource）
	 */
	String dataSource() default "";

	/**
	 * 参与检索的数据库表名，例如:
	 * users u, user_role ur, roles r
	 * v3.0.0 后可空，为空时以类名映射表名
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
	 * 字段未加 {@link DbField } 注解时，指定它自动映射到那张表
	 * 只有在 {@link #tables()} 属性不空时起作用，当 {@link #tables()} 为空时，并且没有添加 {@link DbIgnore } 注解时，则字段自动映射到 {@link DbMapping } 决定的那张表）
	 * 如果 autoMapping 为空，则表示未被 @DbField 注解的字段不需要映射
	 * @since v3.0.0
	 * @return 自动映射的表名 或 别名
	 */
	String autoMapTo() default "";

	/**
	 * 继承类型
	 * @since v3.2.0
	 * @return InheritType
	 */
	InheritType inheritType() default InheritType.DEFAULT;

	/**
	 * @since v3.4.0
	 * @return 需要忽略的属性名
	 */
	String[] ignoreFields() default {};

	/**
	 * @since v3.6.0
	 * @return 默认排序字段信息
	 */
	String orderBy() default "";

	/**
	 * 排序约束类型：
	 * 为 {@link SortType#ALLOW_PARAM } 时，表示：允许使用 检索参数 重新指定排序字段，
	 * 为 {@link SortType#ONLY_ENTITY } 时，表示：只可以 使用本注解的 {@link #orderBy()} 属性指定排序字段，并会忽略 检索参数中的排序信息
	 * 为 {@link SortType#DEFAULT } 时，表示：根据检索器的实例级配置 {@link DefaultDbMapping#getDefaultSortType()} 来决定 是否允许使用 检索参数 重新指定排序字段
	 * @since v3.6.0
	 * @return 默认的排序信息是否可被修改
	 */
	SortType sortType() default SortType.DEFAULT;

}
