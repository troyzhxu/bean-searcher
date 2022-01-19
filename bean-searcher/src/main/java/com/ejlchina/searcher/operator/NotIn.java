package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.implement.DialectWrapper;
import com.ejlchina.searcher.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * NotIn 运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class NotIn extends DialectWrapper implements FieldOp {

    @Override
    public String name() {
        return "NotIn";
    }

    @Override
    public boolean isNamed(String name) {
        return "ni".equals(name) || "NotIn".equals(name);
    }

    @Override
    public boolean lonely() {
        return false;
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        String dbField = opPara.getDbField();
        Object[] values = opPara.getValues();
        if (opPara.isIgnoreCase()) {
            toUpperCase(sqlBuilder, dbField);
            ObjectUtils.upperCase(values);
        } else {
            sqlBuilder.append(dbField);
        }
        List<Object> params = new ArrayList<>();
        sqlBuilder.append(" not in (");
        for (int i = 0; i < values.length; i++) {
            sqlBuilder.append("?");
            params.add(values[i]);
            if (i < values.length - 1) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(")");
        return params;
    }

}
