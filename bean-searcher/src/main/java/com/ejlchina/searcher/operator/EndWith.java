package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.SqlWrapper;
import com.ejlchina.searcher.dialect.DialectWrapper;
import com.ejlchina.searcher.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ejlchina.searcher.util.ObjectUtils.firstNotNull;

/**
 * 起始运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class EndWith extends DialectWrapper implements FieldOp {

    @Override
    public String name() {
        return "EndWith";
    }

    @Override
    public boolean isNamed(String name) {
        return "ew".equals(name) || "EndWith".equals(name);
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        SqlWrapper<Object> fieldSql = opPara.getFieldSql();
        Object[] values = opPara.getValues();
        if (opPara.isIgnoreCase()) {
            if (hasILike()) {
                sqlBuilder.append(fieldSql.getSql()).append(" ilike ?");
            } else {
                toUpperCase(sqlBuilder, fieldSql.getSql());
                sqlBuilder.append(" like ?");
                ObjectUtils.upperCase(values);
            }
        } else {
            sqlBuilder.append(fieldSql.getSql()).append(" like ?");
        }
        List<Object> params = new ArrayList<>(fieldSql.getParas());
        params.add("%" + firstNotNull(values));
        return params;
    }

}
