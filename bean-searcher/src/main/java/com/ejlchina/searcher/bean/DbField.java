package com.ejlchina.searcher.bean;

import com.ejlchina.searcher.DbMapping;
import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldOp;

import java.lang.annotation.*;

/**
 * 用于注解一个可检索 bean 的属性
 * 来指定属性对应数据库的哪张表的哪个字段，可与 {@link SearchBean } 配合使用
 * 不可与 {@link DbIgnore } 在同一字段上使用
 * v3.0.0 后该注解可以缺省，缺省时, 如果 @SearchBean 注解同时缺省 tables 或指定了 autoMapTo，则根据 {@link DbMapping } 自动映射到表字段
 * @author Troy.Zhou @ 2017-03-20
 * @since v1.0.0
 */
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
	 * @since v3.0.0
	 * @return 该字段是否可以被作为检索条件
	 */
	boolean conditional() default true;

	/**
	 * 用于指定该字段只允许接受的运算符，为空时，表示任意运算符都接受
	 * @since v3.0.0
	 * @return Operator[]
	 */
	Class<? extends FieldOp>[] onlyOn() default {};

	/**
	 * 用于指定字段别名，不指定时将自动生成
	 * @since v3.5.0
	 * @return 字段别名
	 */
	String alias() default "";

	/**
	 * 数据库字段类型，当被该注解标记的字段作为检索条件时，该属性用于对用户传入的字段值进行转换
	 * @since v3.8.0
	 * @return DbType
	 */
	DbType type() default DbType.UNKNOWN;

	/**
	 * 绑定FieldConvertor，该Convertor只用于确定场景下的数值转换，不会与其他Convertor冲突
	 * @since v3.8.1
	 * @return Class<? extends FieldConvertor>
	 */
	Class<? extends FieldConvertor> converter() default FieldConvertor.class;

}
