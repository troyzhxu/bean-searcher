package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.SqlWrapper;
import com.ejlchina.searcher.implement.DialectWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * OrLike 运算符
 * @author Troy.Zhou @ 2022-05-23
 * @since v3.7.0
 */
public class OrLike extends DialectWrapper implements FieldOp {

    @Override
    public String name() {
        return "OrLike";
    }

    @Override
    public boolean isNamed(String name) {
        return "ol".equals(name) || "OrLike".equals(name);
    }

    @Override
    public boolean lonely() {
        return false;
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        SqlWrapper<Object> fieldSql = opPara.getFieldSql();
        List<Object> params = new ArrayList<>();
        boolean ic = opPara.isIgnoreCase();
        boolean notFirst = false;
        for (Object value : opPara.getValues()) {
            if (value == null) {
                continue;
            }
            if (notFirst) {
                sqlBuilder.append(" or ");
            } else {
                notFirst = true;
            }
            if (ic) {
                if (hasILike()) {
                    sqlBuilder.append(fieldSql.getSql());
                    sqlBuilder.append(" ilike ?");
                } else {
                    toUpperCase(sqlBuilder, fieldSql.getSql());
                    sqlBuilder.append(" like ?");
                    value = upperCase(value);
                }
            } else {
                sqlBuilder.append(fieldSql.getSql());
                sqlBuilder.append(" like ?");
            }
            params.addAll(fieldSql.getParas());
            params.add(value);
        }
        return params;
    }

    public static String upperCase(Object value) {
        if (value instanceof String) {
            return ((String) value).toUpperCase();
        }
        return value.toString();
    }

}
