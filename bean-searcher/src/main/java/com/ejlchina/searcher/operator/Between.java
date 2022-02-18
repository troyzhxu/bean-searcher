package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.SqlWrapper;
import com.ejlchina.searcher.implement.DialectWrapper;
import com.ejlchina.searcher.util.ObjectUtils;
import com.ejlchina.searcher.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 区间运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class Between extends DialectWrapper implements FieldOp {

    @Override
    public String name() {
        return "Between";
    }

    @Override
    public boolean isNamed(String name) {
        return "bt".equals(name) || "Between".equals(name);
    }

    @Override
    public boolean lonely() {
        return false;
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        SqlWrapper<Object> fieldSql = opPara.getDbFieldSql();
        Object[] values = opPara.getValues();
        if (opPara.isIgnoreCase()) {
            toUpperCase(sqlBuilder, fieldSql.getSql());
            ObjectUtils.upperCase(values);
        } else {
            sqlBuilder.append(fieldSql.getSql());
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
        List<Object> params = new ArrayList<>(fieldSql.getParas());
        if (!val1Null && !val2Null) {
            sqlBuilder.append(" between ? and ? ");
            params.add(value0);
            params.add(value1);
        } else if (val1Null && !val2Null) {
            sqlBuilder.append(" <= ? ");
            params.add(value1);
        } else if (!val1Null) {
            sqlBuilder.append(" >= ? ");
            params.add(value0);
        }
        return params;
    }

}
