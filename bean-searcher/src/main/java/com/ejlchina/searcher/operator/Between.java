package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.dialect.Dialect;
import com.ejlchina.searcher.util.ObjectUtils;
import com.ejlchina.searcher.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * 区间运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class Between implements FieldOp {

    @Override
    public String name() {
        return "Between";
    }

    @Override
    public boolean isNamed(String name) {
        return "bt".equals(name) || "Between".equals(name);
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara, Dialect dialect) {
        String dbField = opPara.getDbField();
        Object[] values = opPara.getValues();
        if (opPara.isIgnoreCase()) {
            dialect.toUpperCase(sqlBuilder, dbField);
            ObjectUtils.upperCase(values);
        } else {
            sqlBuilder.append(dbField);
        }
        boolean val1Null = false;
        boolean val2Null = false;
        Object value0 = values.length > 0 ? values[0] : null;
        Object value1 = values.length > 1 ? values[1] : null;
        if (value0 == null || (value0 instanceof String && StringUtils.isBlank((String) value0))) {
            val1Null = true;
        }
        if (value1 == null || (value1 instanceof String && StringUtils.isBlank((String) value1))) {
            val2Null = true;
        }
        if (!val1Null && !val2Null) {
            sqlBuilder.append(" between ? and ? ");
            return Arrays.asList(value0, value1);
        } else if (val1Null && !val2Null) {
            sqlBuilder.append(" <= ? ");
            return singletonList(value1);
        } else if (!val1Null) {
            sqlBuilder.append(" >= ? ");
            return singletonList(value0);
        }
        return Collections.emptyList();
    }

}
