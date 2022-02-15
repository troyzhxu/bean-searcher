package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.implement.DialectWrapper;
import com.ejlchina.searcher.util.ObjectUtils;

import java.util.List;

import static com.ejlchina.searcher.util.ObjectUtils.firstNotNull;
import static java.util.Collections.singletonList;

/**
 * 小于运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class LessThan extends DialectWrapper implements FieldOp {

    @Override
    public String name() {
        return "LessThan";
    }

    @Override
    public boolean isNamed(String name) {
        return "lt".equals(name) || "LessThan".equals(name);
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
        sqlBuilder.append(" < ?");
        return singletonList(firstNotNull(values));
    }

}