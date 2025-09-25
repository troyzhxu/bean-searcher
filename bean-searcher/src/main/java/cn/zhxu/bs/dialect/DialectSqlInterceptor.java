package cn.zhxu.bs.dialect;

import cn.zhxu.bs.SearchSql;
import cn.zhxu.bs.SqlInterceptor;
import cn.zhxu.bs.param.FetchType;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 方言 SQL 拦截器，在拦截器链中必须放在 {@link DynamicDialectSupport } 之后
 * @author Troy.Zhou
 * @since v4.6.0
 * */
public class DialectSqlInterceptor extends DialectWrapper implements SqlInterceptor {

    // 正则表达式：匹配true和false字面量，前后可以是空格、等于号、逗号，圆括号，或者位于文本中间与结尾
    static final Pattern BOOL_PATTERN = Pattern.compile(
            "[=<>]\\s*(true|false)(\\s|\\)|$)|(\\s|\\()(true|false)\\s*[=<>!]",
            Pattern.CASE_INSENSITIVE
    );

    public DialectSqlInterceptor() {
    }

    public DialectSqlInterceptor(Dialect dialect) {
        super(dialect);
    }

    @Override
    public <T> SearchSql<T> intercept(SearchSql<T> searchSql, Map<String, Object> paraMap, FetchType fetchType) {
        if (searchSql.isShouldQueryList()) {
            searchSql.setListSqlString(processDialect(searchSql.getListSqlString()));
        }
        if (searchSql.isShouldQueryCluster()) {
            searchSql.setClusterSqlString(processDialect(searchSql.getClusterSqlString()));
        }
        return searchSql;
    }

    protected String processDialect(String sql) {
        if (allowBoolLiterals()) {
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

            boolean isTrue = it.contains("t") || it.contains("T");
            int lateralEnd = it.indexOf('e');
            if (lateralEnd < 0) {
                lateralEnd = it.indexOf('E');
            }
            int lateralStart = lateralEnd - (isTrue ? 3 : 4);

            newSql.append(it, 0, lateralStart)
                    .append(isTrue ? 1 : 0)
                    .append(it, lateralEnd + 1, it.length());

            lastIndex = start + it.length();
        }
        if (lastIndex == 0) {
            // 没有匹配到，返回原 SQL
            return sql;
        }
        newSql.append(sql, lastIndex, sql.length());
        return newSql.toString();
    }

}
