package cn.zhxu.bs.operator;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SqlWrapper;

import java.util.List;

/**
 * IsNull 值运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class IsNull implements FieldOp {

    @Override
    public String name() {
        return "IsNull";
    }

    @Override
    public boolean isNamed(String name) {
        return "nl".equals(name) || "IsNull".equals(name);
    }

    @Override
    public boolean lonely() {
        return true;
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        SqlWrapper<Object> fieldSql = opPara.getFieldSql();
        sqlBuilder.append(fieldSql.getSql()).append(" is null");
        return fieldSql.getParas();
    }

}
