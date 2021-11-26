package com.ejlchina.searcher.param;

/**
 * 过滤运算符
 * 
 * @author Troy.Zhou @ 2017-03-20
 *
 */
public enum Operator {

	/**
	 * 等于
	 */
	Equal,
	
	/**
	 * 大于等于
	 */
	GreaterEqual, 
	
	/**
	 * 大于
	 */
	GreaterThan, 
	
	/**
	 * 小于等于
	 */
	LessEqual, 
	
	/**
	 * 小于
	 */
	LessThan, 
	
	/**
	 * 不等于
	 */
	NotEqual, 
	
	/**
	 * 为空
	 */
	Empty, 
	
	/**
	 * 不为空
	 */
	NotEmpty,

	/**
	 * 包含
	 * like '%xxx%'
	 */
	Like,

	/**
	 * 以 .. 开始
	 * like 'xxx%'
	 */
	StartWith, 
	
	/**
	 * 以 .. 结束
	 * like '%xxx'
	 */
	EndWith, 
	
	/**
	 * 在 .. 和 .. 之间
	 */
	Between,

	/**
	 * 在列表..中
	 */
	In,
	/**
	 * 多值
	 * in
	 * @deprecated 建议使用In向SQL对应关键字看齐，语义更加明确
	 * {@link #In}
	 */
	@Deprecated
	MultiValue;

	public static Operator from(String op) {
		switch (op) {
		case "ct":
		case "Contains":
		case "Include":
		case "Like":
			return Like;
		case "eq":
		case "Equal":
			return Equal;
		case "ge":
		case "GreaterEqual":
			return GreaterEqual;
		case "gt":
		case "GreaterThan":
			return GreaterThan;
		case "le":
		case "LessEqual":
			return LessEqual;
		case "lt":
		case "LessThan":
			return LessThan;
		case "ne":
		case "NotEqual":
			return NotEqual;
		case "ey":
		case "Empty":
			return Empty;
		case "ny":
		case "NotEmpty":
			return NotEmpty;
		case "sw":
		case "StartWith":
			return StartWith;
		case "ew":
		case "EndWith":
			return EndWith;
		case "bt":
		case "Between":
			return Between;
		case "in":
		case "In":
			return In;
		case "mv":
		case "MultiValue":
			return MultiValue;
		}
		return null;
	}

}