package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;

import java.util.Collections;
import java.util.List;

/**
 * Null 值运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class Null implements FieldOp {

    @Override
    public boolean isNamed(String name) {
        return "nl".equals(name) || "Null".equals(name);
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, String dbField, Object[] values) {
        sqlBuilder.append(" is null");
        return Collections.emptyList();
    }

}
