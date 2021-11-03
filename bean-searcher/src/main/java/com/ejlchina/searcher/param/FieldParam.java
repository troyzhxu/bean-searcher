package com.ejlchina.searcher.param;

import com.ejlchina.searcher.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 字段参数
 * @author Troy.Zhou @ 2017-03-20
 */
public class FieldParam {

	/**
	 * 字段名
	 */
	private final String name;

	/**
	 * 字段运算符
	 */
	private Operator operator;

	/**
	 * 参数值
	 */
	private final List<Value> values;

	/**
	 * 是否忽略大小写
	 */
	private Boolean ignoreCase;

	/**
	 * 字段参数值
	 */
	public static class Value {

		private final Object value;
		private final int index;

		public Value(Object value, int index) {
			this.value = value;
			this.index = index;
		}

		public boolean isEmptyValue() {
			return value == null || (value instanceof String && StringUtils.isBlank((String) value));
		}

		public Object getValue() {
			return value;
		}

	}

	public FieldParam(String name, Operator operator) {
		this(name, operator, Collections.emptyList(), false);
	}

	public FieldParam(String name, List<Value> values) {
		this.name = name;
		this.values = values;
	}

	public FieldParam(String name, Operator operator, List<Value> values, boolean ignoreCase) {
		this.name = name;
		this.operator = operator;
		this.values = values;
		this.ignoreCase = ignoreCase;
	}

	public String getName() {
		return name;
	}

	public Object[] getValues() {
		values.sort(Comparator.comparingInt(v -> v.index));
		Object[] objects = new Object[values.size()];
		for (int i = 0; i < values.size(); i++) {
			objects[i] = values.get(i).value;
		}
		return objects;
	}

	public List<Value> getValueList() {
		return values;
	}

	public Boolean isIgnoreCase() {
		return ignoreCase;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public void setIgnoreCase(Boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

}
