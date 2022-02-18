package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.SqlWrapper;
import com.ejlchina.searcher.implement.DialectWrapper;
import com.ejlchina.searcher.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * In 运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class InList extends DialectWrapper implements FieldOp {

    @Override
    public String name() {
        return "InList";
    }

    @Override
    public boolean isNamed(String name) {
        return "il".equals(name) || "mv".equals(name) || "InList".equals(name) || "MultiValue".equals(name);
    }

    @Override
    public boolean lonely() {
        return false;
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
        List<Object> params = new ArrayList<>(fieldSql.getParas());
        sqlBuilder.append(" in (");
        for (int i = 0; i < values.length; i++) {
            sqlBuilder.append("?");
            params.add(values[i]);
            if (i < values.length - 1) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(")");
        return params;
    }

}
