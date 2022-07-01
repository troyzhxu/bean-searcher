package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.SqlWrapper;
import com.ejlchina.searcher.dialect.DialectWrapper;
import com.ejlchina.searcher.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ejlchina.searcher.util.ObjectUtils.firstNotNull;

/**
 * 大于运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class GreaterThan extends DialectWrapper implements FieldOp {

    @Override
    public String name() {
        return "GreaterThan";
    }

    @Override
    public boolean isNamed(String name) {
        return "gt".equals(name) || "GreaterThan".equals(name);
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        SqlWrapper<Object> fieldSql = opPara.getFieldSql();
        Object[] values = opPara.getValues();
        if (opPara.isIgnoreCase()) {
            toUpperCase(sqlBuilder, fieldSql.getSql());
            ObjectUtils.upperCase(values);
        } else {
            sqlBuilder.append(fieldSql.getSql());
        }
        sqlBuilder.append(" > ?");
        List<Object> params = new ArrayList<>(fieldSql.getParas());
        params.add(firstNotNull(values));
        return params;
    }

}