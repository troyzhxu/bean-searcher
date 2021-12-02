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
	 * 多值
	 * in
	 */
	MultiValue;

	public static Operator from(Object op) {
		if (op == null) {
			return null;
		}
		if (op instanceof Operator) {
			return (Operator) op;
		}
		switch (op.toString()) {
		case "in":
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
		case "mv":
		case "MultiValue":
			return MultiValue;
		}
		return null;
	}

}