package cn.zhxu.bs.operator;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.dialect.DialectWrapper;
import cn.zhxu.bs.util.MapUtils;
import cn.zhxu.bs.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义 SQL 运算符，改运算符只能通过参数构建器 {@link MapUtils#builder() } 使用
 * @author Troy.Zhou @ 2022-06-27
 * @since v3.8.0
 */
public class SqlCond extends DialectWrapper implements FieldOp {

    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\d+|\\?");

    public static final Object[] EMPTY_ARGS = new Object[0];

    private final String sqlCond;
    private final Object[] args;

    public SqlCond(String sqlCond) {
        this(sqlCond, EMPTY_ARGS);
    }

    public SqlCond(String sqlCond, Object[] args) {
        this.sqlCond = Objects.requireNonNull(sqlCond);
        this.args = Objects.requireNonNull(args);
        if (StringUtils.countOf(sqlCond, '?') != args.length) {
            throw new IllegalStateException("The count of '?' in [" + sqlCond + "] is not equal to the count of args: " + Arrays.toString(args));
        }
    }

    @Override
    public String name() {
        return "SqlCond";
    }

    @Override
    public boolean isNamed(String name) {
        return "sql".equals(name) || "SqlCond".equals(name);
    }

    @Override
    public boolean lonely() {
        return true;
    }

    @Override
    public boolean isNonPublic() {
        return true;
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, OpPara opPara) {
        List<Object> params = new ArrayList<>();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(sqlCond);
        int lastIndex = 0;
        int argIndex = 0;
        while (matcher.find()) {
            String placeholder = matcher.group();
            if ("?".equals(placeholder)) {
                params.add(args[argIndex++]);
                continue;
            }
            int index = Integer.parseInt(placeholder.substring(1));
            SqlWrapper<Object> fieldSql = getFieldSql(opPara, index);
            int start = matcher.start();
            if (start > lastIndex) {
                sqlBuilder.append(sqlCond, lastIndex, start);
            }
            sqlBuilder.append(fieldSql.getSql());
            params.addAll(fieldSql.getParas());
            lastIndex = start + placeholder.length();
        }
        sqlBuilder.append(sqlCond, lastIndex, sqlCond.length());
        return params;
    }

    private SqlWrapper<Object> getFieldSql(OpPara opPara, int index) {
        if (index < 1) {
            throw new IllegalStateException("Invalid sql placeholder: $" + index + ", the index must start from 1");
        }
        if (index == 1) {
            return opPara.getFieldSql();
        }
        Object[] values = opPara.getValues();
        if (values.length < index - 1) {
            throw new IllegalStateException("Invalid placeholder: $" + index + ", the index out of the field's count: " + Arrays.toString(values));
        }
        Object value = values[index - 2];
        if (value == null) {
            throw new IllegalStateException("Invalid filed [null] at $" + index);
        }
        return opPara.getFieldSql(value.toString());
    }

}
