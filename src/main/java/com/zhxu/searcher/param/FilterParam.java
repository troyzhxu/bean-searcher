package com.zhxu.searcher.param;

/**
 * @author Troy.Zhou @ 2017-03-20
 *
 * 过滤检索参数
 * 
 */
public class FilterParam {

	private String name;
	private String value;
	private String value2;
	private boolean ignoreCase;
	private FilterOperator operator;

	@Override
	public String toString() {
		return "FilterParam [name=" + name + ", value=" + value + ", value2=" + value2 + ", ignoreCase=" + ignoreCase
				+ ", operator=" + operator + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public FilterOperator getOperator() {
		return operator;
	}

	public void setOperator(FilterOperator operator) {
		this.operator = operator;
	}

}
