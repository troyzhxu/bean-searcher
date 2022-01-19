package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOperator;
import com.ejlchina.searcher.util.ObjectUtils;

import java.util.Collections;
import java.util.List;

/**
 * 等于运算符
 */
public class Equal implements FieldOperator {

    @Override
    public boolean isNamed(String name) {
        return "eq".equals(name) || "Equal".equals(name);
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, String dbField, Object[] values) {
        sqlBuilder.append(" = ?");
        return Collections.singletonList(ObjectUtils.firstNotNull(values));
    }

}
