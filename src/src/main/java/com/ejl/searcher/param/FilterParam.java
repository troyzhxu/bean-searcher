package com.ejl.searcher.param;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.ejl.searcher.util.StrUtils;

/**
 * 过滤检索参数
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 */
public class FilterParam {

	private String name;
	private List<Value> values = new ArrayList<>(2);
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

	public void addValue(String value) {
		values.add(new Value(value, 0));
	}
	
	public void addValue(String value, int index) {
		values.add(new Value(value, index));
	}

	public String[] getValues() {
		values.sort(new Comparator<Value>() {
			@Override
			public int compare(Value v1, Value v2) {
				return v1.sort - v2.sort;
			}
		});
		String[] vals = new String[values.size()];
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
			if (!StrUtils.isBlank(value.value)) {
				return false;
			}
		}
		return true;
	}

	public String firstNotNullValue() {
		for (Value value : values) {
			if (!StrUtils.isBlank(value.value)) {
				return value.value;
			}
		}
		return null;
	}
	

	public static class Value {

		public Value(String value, int sort) {
			super();
			this.value = value;
			this.sort = sort;
		}

		private String value;
		private int sort;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
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
