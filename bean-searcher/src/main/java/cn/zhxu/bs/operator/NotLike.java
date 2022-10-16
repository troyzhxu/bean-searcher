package cn.zhxu.bs.operator;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.dialect.DialectWrapper;
import cn.zhxu.bs.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

import static cn.zhxu.bs.util.ObjectUtils.firstNotNull;

/**
 * NotLike 运算符，用法：
 * <pre>{@code
 * Map<String, Object> params = MapUtils.builder()
 *     .field(User::getName, "张%").op(NotLike.class)
 *     .build();
 * List<User> users = beanSearcher.searchList(User.class, params);
 * // 生成的 SQL：
 * select * from user where name not like '张%'
 * }</pre>
 * @author Troy.Zhou @ 2022-06-19
 * @since v3.8.0
 */
public class NotLike extends DialectWrapper implements FieldOp {

    @Override
    public String name() {
        return "NotLike";
    }

    @Override
    public boolean isNamed(String name) {
        return "nk".equals(name) || "NotLike".equals(name);
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        SqlWrapper<Object> fieldSql = opPara.getFieldSql();
        Object[] values = opPara.getValues();
        if (opPara.isIgnoreCase()) {
            if (hasILike()) {
                sqlBuilder.append(fieldSql.getSql()).append(" not ilike ?");
            } else {
                toUpperCase(sqlBuilder, fieldSql.getSql());
                sqlBuilder.append(" not like ?");
                ObjectUtils.upperCase(values);
            }
        } else {
            sqlBuilder.append(fieldSql.getSql()).append(" not like ?");
        }
        List<Object> params = new ArrayList<>(fieldSql.getParas());
        params.add(firstNotNull(values));
        return params;
    }

}
