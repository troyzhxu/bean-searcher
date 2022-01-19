package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;

import java.util.ArrayList;
import java.util.List;

/**
 * NotIn 运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class NotIn implements FieldOp {

    @Override
    public boolean isNamed(String name) {
        return "ni".equals(name) || "NotIn".equals(name);
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, String dbField, Object[] values) {
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
