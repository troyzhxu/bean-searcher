package cn.zhxu.bs;

import cn.zhxu.bs.operator.*;

/**
 * 过滤运算符
 * @author Troy.Zhou @ 2017-03-20
 */
public class FieldOps {

	/**
	 * 等于
	 */
	public static final Equal Equal = new Equal();

	/**
	 * 不等于
	 */
	public static final NotEqual NotEqual = new NotEqual();

	/**
	 * 大于等于
	 */
	public static final GreaterEqual GreaterEqual = new GreaterEqual();
	
	/**
	 * 大于
	 */
	public static final GreaterThan GreaterThan = new GreaterThan();
	
	/**
	 * 小于等于
	 */
	public static final LessEqual LessEqual = new LessEqual();
	
	/**
	 * 小于
	 */
	public static final LessThan LessThan = new LessThan();

	/**
	 * 为 null
	 */
	public static final IsNull IsNull = new IsNull();

	/**
	 * 不为 null
	 */
	public static final NotNull NotNull = new NotNull();

	/**
	 * 为空
	 */
	public static final Empty Empty = new Empty();
	
	/**
	 * 不为空
	 */
	public static final NotEmpty NotEmpty = new NotEmpty();

	/**
	 * 包含
	 * like '%xxx%'
	 */
	public static final Contain Contain = new Contain();

	/**
	 * 以 .. 开始
	 * like 'xxx%'
	 */
	public static final StartWith StartWith = new StartWith();
	
	/**
	 * 以 .. 结束
	 * like '%xxx'
	 */
	public static final EndWith EndWith = new EndWith();

	/**
	 * like {v1} or like {v1}
	 */
	public static final OrLike OrLike = new OrLike();

	/**
	 * not like {v}
	 */
	public static final NotLike NotLike = new NotLike();

	/**
	 * 在 .. 和 .. 之间
	 */
	public static final Between Between = new Between();

	/**
	 * 不在 .. 和 .. 之间
	 */
	public static final NotBetween NotBetween = new NotBetween();

	/**
	 * 在列表中
	 * in (...)
	 */
	public static final InList InList = new InList();

	/**
	 * 不在某个集合内
	 * not in
	 * @since v3.3
	 */
	public static final NotIn NotIn = new NotIn();

}