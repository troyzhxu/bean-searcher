package com.ejlchina.searcher.param;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.ejlchina.searcher.util.StringUtils;

/**
 * 过滤检索参数
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 */
public class FilterParam {

	private String name;
	private final List<Value> values = new ArrayList<>(2);
	private boolean ignoreCase;
	private Operator operator;

	@Override
	public String toString() {
		return "FilterParam [name=" + name + ", values=" + values + ", ignoreCase=" + ignoreCase + ", operator="
				+ operator + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addValue(Object value) {
		values.add(new Value(value, 0));
	}
	
	public void addValue(Object value, int index) {
		values.add(new Value(value, index));
	}

	public Object[] getValues() {
		values.sort(Comparator.comparingInt(v -> v.sort));
		Object[] vals = new Object[values.size()];
		for (int i = 0; i < values.size(); i++) {
			vals[i] = values.get(i).value;
		}
		return vals;
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public boolean allValuesEmpty() {
		for (Value value : values) {
			if (!value.isEmptyValue()) {
				return false;
			}
		}
		return true;
	}
	

	public static class Value {

		public Value(Object value, int sort) {
			super();
			this.value = value;
			this.sort = sort;
		}

		private Object value;
		private int sort;

		
		public boolean isEmptyValue() {
			return value == null || (value instanceof String && StringUtils.isBlank((String) value));
		}
		
		
		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
		
		public int getSort() {
			return sort;
		}

		public void setSort(int sort) {
			this.sort = sort;
		}

		@Override
		public String toString() {
			return "Value [value=" + value + ", sort=" + sort + "]";
		}
		
	}

}
