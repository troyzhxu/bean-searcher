package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOperator;
import com.ejlchina.searcher.util.ObjectUtils;

import java.util.Collections;
import java.util.List;

public class Contain implements FieldOperator {

    @Override
    public boolean isNamed(String name) {
        return "ct".equals(name) || "Contain".equals(name);
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, String dbField, Object[] values) {
        sqlBuilder.append(" like ?");
        return Collections.singletonList("%" + ObjectUtils.firstNotNull(values) + "%");
    }

}