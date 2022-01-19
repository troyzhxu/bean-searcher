package com.ejlchina.searcher.implement;

import java.util.regex.Pattern;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.param.Operator;

/**
 * 日期参数值矫正器
 * @author Troy.Zhou @ 2021-11-01
 * @since v3.0.0
 */
public class DateValueCorrector {

	static final Pattern DATE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

	static final Pattern DATE_HOUR_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}");
	
	static final Pattern DATE_MINUTE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}");

	/**
	 * 日期参数值矫正处理
	 * @param dateValues 参数值
	 * @param operator 字段运算符
	 * @return 矫正后的日期参数
	 */
	public Object[] correct(Object[] dateValues, FieldOp operator) {
		if (operator.sameTo(Operator.LessThan) || operator.sameTo(Operator.GreaterEqual)) {
			for (int i = 0; i <dateValues.length; i++) {
				dateValues[i] = dateValue(dateValues[i], true);
			}
		}
		if (operator.sameTo(Operator.LessEqual) || operator.sameTo(Operator.GreaterThan)) {
			for (int i = 0; i <dateValues.length; i++) {
				dateValues[i] = dateValue(dateValues[i], false);
			}
		}
		if (operator.sameTo(Operator.Between)) {
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

	
}
