package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.operator.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 日期参数值矫正器
 * @author Troy.Zhou @ 2021-11-01
 * @since v3.0.0
 */
public class DateValueCorrector {

	static final Pattern DATE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

	static final Pattern DATE_HOUR_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}");
	
	static final Pattern DATE_MINUTE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}");

	private FieldOp lt = new LessThan();
	private FieldOp ge = new GreaterEqual();
	private FieldOp le = new LessEqual();
	private FieldOp gt = new GreaterThan();
	private FieldOp bt = new Between();
	private FieldOp nb = new NotBetween();

	/**
	 * 日期参数值矫正处理
	 * @param fieldType 字段类型
	 * @param dateValues 参数值
	 * @param operator 字段运算符
	 * @return 矫正后的日期参数
	 */
	public Object[] correct(Class<?> fieldType, Object[] dateValues, FieldOp operator) {
		if (!Date.class.isAssignableFrom(fieldType) && LocalDateTime.class != fieldType) {
			return dateValues;
		}
		if (operator.sameTo(lt) || operator.sameTo(ge)) {
			for (int i = 0; i < dateValues.length; i++) {
				dateValues[i] = dateValue(dateValues[i], true);
			}
		}
		if (operator.sameTo(le) || operator.sameTo(gt)) {
			for (int i = 0; i < dateValues.length; i++) {
				dateValues[i] = dateValue(dateValues[i], false);
			}
		}
		if (operator.sameTo(bt) || operator.sameTo(nb)) {
			if (dateValues.length > 0) {
				dateValues[0] = dateValue(dateValues[0], true);
			}
			if (dateValues.length > 1) {
				dateValues[1] = dateValue(dateValues[1], false);
			}
		}
		return dateValues;
	}


	protected Object dateValue(Object value, boolean roundDown) {
		if (value instanceof String) {
			String strValue = (String) value;
			if (DATE_PATTERN.matcher(strValue).matches()) {
				if (roundDown) {
					return strValue + " 00:00:00";
				} else {
					return strValue + " 23:59:59";
				}
			} else if (DATE_HOUR_PATTERN.matcher(strValue).matches()) {
				if (roundDown) {
					return strValue + ":00:00";
				} else {
					return strValue + ":59:59";
				}
			} else if (DATE_MINUTE_PATTERN.matcher(strValue).matches()) {
				if (roundDown) {
					return strValue + ":00";
				} else {
					return strValue + ":59";
				}
			}
		}
		return value;
	}

	public FieldOp getLt() {
		return lt;
	}

	public void setLt(FieldOp lt) {
		this.lt = lt;
	}

	public FieldOp getGe() {
		return ge;
	}

	public void setGe(FieldOp ge) {
		this.ge = ge;
	}

	public FieldOp getLe() {
		return le;
	}

	public void setLe(FieldOp le) {
		this.le = le;
	}

	public FieldOp getGt() {
		return gt;
	}

	public void setGt(FieldOp gt) {
		this.gt = gt;
	}

	public FieldOp getBt() {
		return bt;
	}

	public void setBt(FieldOp bt) {
		this.bt = bt;
	}

	public FieldOp getNb() {
		return nb;
	}

	public void setNb(FieldOp nb) {
		this.nb = nb;
	}

}
