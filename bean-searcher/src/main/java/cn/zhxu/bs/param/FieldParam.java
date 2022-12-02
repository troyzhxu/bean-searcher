package cn.zhxu.bs.param;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.util.StringUtils;

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
	private Object operator;

	/**
	 * 参数值
	 */
	private final List<Value> values;

	/**
	 * 是否忽略大小写
	 */
	private boolean ignoreCase;

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

	public FieldParam(String name, FieldOp operator) {
		this(name, operator, Collections.emptyList(), false);
	}

	public FieldParam(String name, List<Value> values) {
		this.name = name;
		this.values = values;
	}

	public FieldParam(String name, FieldOp operator, List<Value> values, boolean ignoreCase) {
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

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public Object getOperator() {
		return operator;
	}

	public void setOperator(Object operator) {
		this.operator = operator;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

}
