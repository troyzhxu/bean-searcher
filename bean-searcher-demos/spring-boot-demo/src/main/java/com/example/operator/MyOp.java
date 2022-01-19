package com.example.operator;

import com.ejlchina.searcher.FieldOp;

import java.util.Collections;
import java.util.List;

/**
 * 自定义运算符
 */
public class MyOp implements FieldOp {

    @Override
    public String name() {
        return "my";
    }

    @Override
    public boolean isNamed(String name) {
        return "my".equals(name);
    }

    @Override
    public boolean lonely() {
        return true;
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        sqlBuilder.append(opPara.getDbField()).append(" = 20");
        return Collections.emptyList();
    }
}
