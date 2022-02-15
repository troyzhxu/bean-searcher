package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.implement.DialectWrapper;
import com.ejlchina.searcher.util.ObjectUtils;

import java.util.List;

import static com.ejlchina.searcher.util.ObjectUtils.firstNotNull;
import static java.util.Collections.singletonList;

/**
 * 包含运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class Contain extends DialectWrapper implements FieldOp {

    @Override
    public String name() {
        return "Contain";
    }

    @Override
    public boolean isNamed(String name) {
        return "ct".equals(name) || "in".equals(name) || "Contain".equals(name) || "Include".equals(name);
    }

    @Override
    public boolean lonely() {
        return false;
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        String dbField = opPara.getDbFieldSql();
        Object[] values = opPara.getValues();
        if (opPara.isIgnoreCase()) {
            toUpperCase(sqlBuilder, dbField);
            ObjectUtils.upperCase(values);
        } else {
            sqlBuilder.append(dbField);
        }
        sqlBuilder.append(" like ?");
        return singletonList("%" + firstNotNull(values) + "%");
    }

}