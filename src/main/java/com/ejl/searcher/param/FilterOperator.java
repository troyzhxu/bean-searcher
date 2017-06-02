package com.ejl.searcher.param;

/**
 * @author Troy.Zhou @ 2017-03-20
 *
 *         过滤运算符
 * 
 */
public enum FilterOperator {

	Include, Equal, GreaterThan, LessThan, NotEqual, Empty, NotEmpty, StartWith, EndWith, Between;

	public static FilterOperator from(String op) {
		switch (op) {
		case "in":
			return Include;
		case "eq":
			return Equal;
		case "gt":
			return GreaterThan;
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
		}
		return Equal;
	}

}