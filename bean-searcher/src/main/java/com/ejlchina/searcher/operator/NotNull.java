package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.dialect.Dialect;
import com.ejlchina.searcher.util.ObjectUtils;

import java.util.Collections;
import java.util.List;

/**
 * 非 IsNull 运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class NotNull implements FieldOp {

    @Override
    public String name() {
        return "NotNull";
    }

    @Override
    public boolean isNamed(String name) {
        return "nn".equals(name) || "NotNull".equals(name);
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara, Dialect dialect) {
        String dbField = opPara.getDbField();
        sqlBuilder.append(dbField).append(" is not null");
        return Collections.emptyList();
    }

}
