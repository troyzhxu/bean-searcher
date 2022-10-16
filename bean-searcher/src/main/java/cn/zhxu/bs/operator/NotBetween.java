package cn.zhxu.bs.operator;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.dialect.DialectWrapper;
import cn.zhxu.bs.util.StringUtils;
import cn.zhxu.bs.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 区间运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class NotBetween extends DialectWrapper implements FieldOp {

    @Override
    public String name() {
        return "NotBetween";
    }

    @Override
    public boolean isNamed(String name) {
        return "nb".equals(name) || "NotBetween".equals(name);
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
        boolean val1Null = false;
        boolean val2Null = false;
        Object value0 = values.length > 0 ? values[0] : null;
        Object value1 = values.length > 1 ? values[1] : null;
        if (value0 == null || (value0 instanceof String && StringUtils.isBlank((String) value0))) {
            val1Null = true;
        }
        if (value1 == null || (value1 instanceof String && StringUtils.isBlank((String) value1))) {
            val2Null = true;
        }
        List<Object> params = new ArrayList<>(fieldSql.getParas());
        if (!val1Null && !val2Null) {
            sqlBuilder.append(" not between ? and ? ");
            params.add(value0);
            params.add(value1);
        } else if (val1Null && !val2Null) {
            sqlBuilder.append(" > ? ");
            params.add(value1);
        } else if (!val1Null) {
            sqlBuilder.append(" < ? ");
            params.add(value0);
        }
        return params;
    }

}
