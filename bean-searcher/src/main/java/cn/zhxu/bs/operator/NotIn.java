package cn.zhxu.bs.operator;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.dialect.DialectWrapper;
import cn.zhxu.bs.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * NotIn 运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class NotIn extends DialectWrapper implements FieldOp {

    @Override
    public String name() {
        return "NotIn";
    }

    @Override
    public boolean isNamed(String name) {
        return "ni".equals(name) || "NotIn".equals(name);
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
        sqlBuilder.append(" not in (");
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
