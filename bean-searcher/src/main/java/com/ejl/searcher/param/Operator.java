package com.ejl.searcher.param;

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
			return Include;
		case "eq":
			return Equal;
		case "ge":
			return GreaterEqual;
		case "gt":
			return GreaterThan;
		case "le":
			return LessEqual;
		case "lt":
			return LessThan;
		case "ne":
			return NotEqual;
		case "ey":
			return Empty;
		case "ny":
			return NotEmpty;
		case "sw":
			return StartWith;
		case "ew":
			return EndWith;
		case "bt":
			return Between;
		case "mv":
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