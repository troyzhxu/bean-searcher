package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;

import java.util.Collections;
import java.util.List;

/**
 * 非空运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class NotEmpty implements FieldOp {

    @Override
    public String name() {
        return "NotEmpty";
    }

    @Override
    public boolean isNamed(String name) {
        return "ny".equals(name) || "NotEmpty".equals(name);
    }

    @Override
    public boolean lonely() {
        return true;
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        String dbField = opPara.getDbField();
        sqlBuilder.append(dbField).append(" is not null");
        sqlBuilder.append(" and ").append(dbField).append(" != ''");
        return Collections.emptyList();
    }

}
