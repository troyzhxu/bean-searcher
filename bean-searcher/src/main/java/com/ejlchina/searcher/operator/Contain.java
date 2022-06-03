package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.SqlWrapper;
import com.ejlchina.searcher.implement.DialectWrapper;
import com.ejlchina.searcher.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.ejlchina.searcher.util.ObjectUtils.firstNotNull;

/**
 * 包含运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class Contain extends DialectWrapper implements FieldOp {

    static final Logger log = LoggerFactory.getLogger(Contain.class);

    @Override
    public String name() {
        return "Contain";
    }

    @Override
    public boolean isNamed(String name) {
        if ("in".equals(name)) {
            log.warn("FieldOp 'in' is deprecated from v3.2.0, please use 'ct' instead.");
            return true;
        }
        if ("Include".equals(name)) {
            log.warn("FieldOp 'Include' is deprecated from v3.2.0, please use 'Contain' instead.");
            return true;
        }
        return "ct".equals(name) || "Contain".equals(name);
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
        params.add("%" + firstNotNull(values) + "%");
        return params;
    }

}