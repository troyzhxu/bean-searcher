package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.dialect.Dialect;
import com.ejlchina.searcher.util.ObjectUtils;

import java.util.Collections;
import java.util.List;

/**
 * 空值运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class Empty implements FieldOp {

    @Override
    public String name() {
        return "Empty";
    }

    @Override
    public boolean isNamed(String name) {
        return "ey".equals(name) || "Empty".equals(name);
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara, Dialect dialect) {
        String dbField = opPara.getDbField();
        sqlBuilder.append(dbField).append(" is null");
        sqlBuilder.append(" or ").append(dbField).append(" = ''");
        return Collections.emptyList();
    }

}
