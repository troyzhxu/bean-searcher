package com.ejlchina.searcher.param;

/**
 * 过滤运算符
 * 
 * @author Troy.Zhou @ 2017-03-20
 *
 */
public enum Operator {

	Include, 
	Equal,
	GreaterEqual, 
	GreaterThan, 
	LessEqual, 
	LessThan, 
	NotEqual, 
	Empty, 
	NotEmpty, 
	StartWith, 
	EndWith, 
	Between, 
	MultiValue; // 多值（或）

	public static Operator from(String op) {
		switch (op) {
		case "in":
		case "Include":
			return Include;
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
		return Equal;
	}
	
	public String val() {
		switch (this) {
		case Include:
			return "in";
		case Between:
			return "bt";
		case Empty:
			return "ey";
		case NotEmpty:
			return "ny";
		case GreaterEqual:
			return "ge";
		case GreaterThan:
			return "gt";
		case LessEqual:
			return "le";
		case LessThan:
			return "lt";
		case MultiValue:
			return "mv";
		case NotEqual:
			return "ne";
		case StartWith:
			return "sw";
		case EndWith:
			return "ew";
		default:
			return "eq";
		}
	}

}