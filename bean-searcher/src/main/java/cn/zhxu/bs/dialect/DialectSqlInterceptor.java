package cn.zhxu.bs.dialect;

import cn.zhxu.bs.SearchSql;
import cn.zhxu.bs.SqlInterceptor;
import cn.zhxu.bs.param.FetchType;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 方言 SQL 拦截器，在拦截器链中必须放在 {@link DynamicDialectSupport } 之后
 * @author Troy.Zhou
 * @since v4.6.0
 * */
public class DialectSqlInterceptor extends DialectWrapper implements SqlInterceptor {

    // 正则表达式：匹配true和false字面量，前后可以是空格、等于号、逗号，圆括号，或者位于文本开头与结尾
    static final Pattern BOOL_PATTERN = Pattern.compile("(?<=^|[ ,=<>()])(true|false)(?=$|[ ,=<>()])", Pattern.CASE_INSENSITIVE);

    public DialectSqlInterceptor() {
    }

    public DialectSqlInterceptor(Dialect dialect) {
        super(dialect);
    }

    @Override
    public <T> SearchSql<T> intercept(SearchSql<T> searchSql, Map<String, Object> paraMap, FetchType fetchType) {
        if (searchSql.isShouldQueryList()) {
            searchSql.setListSqlString(processDialect(searchSql.getListSqlString(), searchSql.getListSqlParams()));
        }
        if (searchSql.isShouldQueryCluster()) {
            searchSql.setClusterSqlString(processDialect(searchSql.getClusterSqlString(), searchSql.getClusterSqlParams()));
        }
        return searchSql;
    }

    protected String processDialect(String sql, List<Object> params) {
        // TODO: 处理其它方言特性

        if (allowBoolParams()) {
            return sql;
        }
        // 处理 true/false 字面量
        Matcher matcher = BOOL_PATTERN.matcher(sql);
        // 存储所有匹配到的结果
        StringBuilder newSql = new StringBuilder();
        int lastIndex = 0;
        while (matcher.find()) {
            int start = matcher.start();
            newSql.append(sql, lastIndex, start);
            String it = matcher.group();
            // true 替换为 1，false 替换为 0
            newSql.append(it.startsWith("t") || it.startsWith("T") ? 1 : 0);
            lastIndex = start + it.length();
        }
        if (lastIndex == 0) {
            // 没有匹配到，返回原 SQL
            return sql;
        }
        return newSql.toString();
    }

}
