package com.ejlchina.searcher.implement.processor;

import java.util.regex.Pattern;

import com.ejlchina.searcher.param.Operator;



public class DefaultParamProcessor implements ParamProcessor {

	
	static final Pattern DATE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

	static final Pattern DATE_HOUR_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}");
	
	static final Pattern DATE_MINUTE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}");
	
	static final Pattern DATE_SECOND_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}");

	
	@Override
	public Object[] upperCase(Object[] params) {
		for (int i = 0; i < params.length; i++) {
			Object val = params[i];
			if (val != null) {
				if (val instanceof String) {
					params[i] = ((String) val).toUpperCase();
				} else {
					params[i] = val;
				}
			}
		}
		return params;
	}

	
	@Override
	public Object[] dateParams(Object[] params, Operator operator) {
		switch (operator) {
		case LessThan:
		case GreaterEqual:
			for (int i = 0; i <params.length; i++) {
				params[i] = dateValue(params[i], true);
			}
			break;
		case LessEqual:
		case GreaterThan:
			for (int i = 0; i <params.length; i++) {
				params[i] = dateValue(params[i], false);
			}
			break;
		case Between:
			if (params.length > 0) {
				params[0] = dateValue(params[0], true);
			}
			if (params.length > 1) {
				params[1] = dateValue(params[1], false);
			}
			break;
		default:
			break;
		}
		return params;
	}


	private Object dateValue(Object value, boolean roundDown) {
		if (value != null && value instanceof String) {
			String strValue = (String) value;
			if (DATE_PATTERN.matcher(strValue).matches()) {
				if (roundDown) {
					return strValue + " 00:00:00";
				} else {
					return strValue + " 59:59:59";
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
