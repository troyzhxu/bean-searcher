package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * In 运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class InList implements FieldOp {

    @Override
    public String name() {
        return "InList";
    }

    @Override
    public boolean isNamed(String name) {
        return "il".equals(name) || "mv".equals(name) || "InList".equals(name) || "MultiValue".equals(name);
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, String dbField, Object[] values) {
        List<Object> params = new ArrayList<>();
        sqlBuilder.append(" in (");
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
