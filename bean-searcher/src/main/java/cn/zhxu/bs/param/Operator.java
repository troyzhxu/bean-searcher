package cn.zhxu.bs.param;

import cn.zhxu.bs.operator.*;

/**
 * 过滤运算符
 * 
 * @author Troy.Zhou @ 2017-03-20
 *
 */
public class Operator {

	/**
	 * 等于
	 */
	public static final Class<cn.zhxu.bs.operator.Equal> Equal = Equal.class;

	/**
	 * 不等于
	 */
	public static final Class<NotEqual> NotEqual = NotEqual.class;

	/**
	 * 大于等于
	 */
	public static final Class<GreaterEqual> GreaterEqual = GreaterEqual.class;
	
	/**
	 * 大于
	 */
	public static final Class<cn.zhxu.bs.operator.GreaterThan> GreaterThan = GreaterThan.class;
	
	/**
	 * 小于等于
	 */
	public static final Class<LessEqual> LessEqual = LessEqual.class;
	
	/**
	 * 小于
	 */
	public static final Class<LessThan> LessThan = LessThan.class;

	/**
	 * 为 null
	 */
	public static final Class<IsNull> IsNull = IsNull.class;

	/**
	 * 不为 null
	 */
	public static final Class<NotNull> NotNull = NotNull.class;

	/**
	 * 为空
	 */
	public static final Class<Empty> Empty = Empty.class;
	
	/**
	 * 不为空
	 */
	public static final Class<NotEmpty> NotEmpty = NotEmpty.class;

	/**
	 * 包含
	 * like '%xxx%'
	 */
	public static final Class<cn.zhxu.bs.operator.Contain> Contain = Contain.class;

	/**
	 * 以 .. 开始
	 * like 'xxx%'
	 */
	public static final Class<StartWith> StartWith = StartWith.class;
	
	/**
	 * 以 .. 结束
	 * like '%xxx'
	 */
	public static final Class<EndWith> EndWith = EndWith.class;
	
	/**
	 * 在 .. 和 .. 之间
	 */
	public static final Class<Between> Between = Between.class;

	/**
	 * 不在 .. 和 .. 之间
	 */
	public static final Class<cn.zhxu.bs.operator.NotBetween> NotBetween = NotBetween.class;

	/**
	 * 在列表中
	 * in (...)
	 */
	public static final Class<cn.zhxu.bs.operator.InList> InList = InList.class;

	/**
	 * 多值
	 * in (...)
	 * Deprecated from v3.3.0
	 * @see #InList
	 */
	@Deprecated
	public static final Class<InList> MultiValue = InList;

	/**
	 * 不在某个集合内
	 * not in
	 * @since v3.3
	 */
	public static final Class<NotIn> NotIn = NotIn.class;

}