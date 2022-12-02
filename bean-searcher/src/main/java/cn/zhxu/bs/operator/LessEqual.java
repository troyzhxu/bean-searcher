package cn.zhxu.bs.operator;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.dialect.DialectWrapper;
import cn.zhxu.bs.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

import static cn.zhxu.bs.util.ObjectUtils.firstNotNull;

/**
 * 小于等于运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class LessEqual extends DialectWrapper implements FieldOp {

    @Override
    public String name() {
        return "LessEqual";
    }

    @Override
    public boolean isNamed(String name) {
        return "le".equals(name) || "LessEqual".equals(name);
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
        sqlBuilder.append(" <= ?");
        List<Object> params = new ArrayList<>(fieldSql.getParas());
        params.add(firstNotNull(values));
        return params;
    }

}
