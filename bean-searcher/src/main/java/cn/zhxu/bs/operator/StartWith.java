package cn.zhxu.bs.operator;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.dialect.DialectWrapper;
import cn.zhxu.bs.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

import static cn.zhxu.bs.util.ObjectUtils.firstNotNull;

/**
 * 起始运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class StartWith extends DialectWrapper implements FieldOp {

    @Override
    public String name() {
        return "StartWith";
    }

    @Override
    public boolean isNamed(String name) {
        return "sw".equals(name) || "StartWith".equals(name);
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
                ObjectUtils.upperCase(values);
                sqlBuilder.append(" like ?");
            }
        } else {
            sqlBuilder.append(fieldSql.getSql()).append(" like ?");
        }
        List<Object> params = new ArrayList<>(fieldSql.getParas());
        params.add(firstNotNull(values) + "%");
        return params;
    }

}
