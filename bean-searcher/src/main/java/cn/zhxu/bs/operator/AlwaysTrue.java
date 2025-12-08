package cn.zhxu.bs.operator;

import cn.zhxu.bs.FieldOp;

import java.util.Collections;
import java.util.List;

/**
 * 恒真运算符,用于忽略字段参数值, 生成 1 条件
 * @author Corey @ 2024-06-06
 * @since v4.3
 */
public class AlwaysTrue implements FieldOp {

    @Override
    public String name() {
        return "AlwaysTrue";
    }

    @Override
    public boolean isNamed(String name) {
        return "at".equals(name) || "AlwaysTrue".equals(name);
    }

    @Override
    public boolean lonely() {
        return true;
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        sqlBuilder.append("1=1");
        return Collections.emptyList();
    }

}
