package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.EmbedParam;
import com.ejlchina.searcher.EmbedParamResolver;
import com.ejlchina.searcher.EmbedSolution;
import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.util.StringUtils;

/**
 * 默认内嵌参数解析器
 * @author Troy.Zhou @ 2017-03-20
 * @since v3.0.0
 */
public class DefaultEmbedParamResolver implements EmbedParamResolver {

    private String paramPrefix = ":";

    private String[] paramEndFlags = new String[] {" ", "\t", "\n", "\r", "+", "-", "*", "/", "=", "!", ">", "<", ",", ")", "'", "%"};

    private final char[] quotations = new char[] {'\'', '"'};

    @Override
    public EmbedSolution resolve(String sqlSnippet) {
        EmbedSolution solution = new EmbedSolution();
        int index1 = sqlSnippet.indexOf(paramPrefix);
        while (index1 >= 0) {
            int index2 = findParamEndIndex(sqlSnippet, index1);
            String sqlName = getSqlName(sqlSnippet, index1, index2);
            if (StringUtils.isBlank(sqlName) || sqlName.length() < 2) {
                throw new SearcherException("There is a syntax error about embed param: " + sqlSnippet);
            }
            EmbedParam embedParam = new EmbedParam(sqlName);
            boolean endWithPrefix = sqlName.endsWith(paramPrefix);
            if (endWithPrefix) {
                embedParam.setName(sqlName.substring(1, sqlName.length() - paramPrefix.length()));
            } else {
                embedParam.setName(sqlName.substring(1));
            }
            int quotationCount1 = StringUtils.containCount(sqlSnippet, 0, index1, quotations);
            int quotationCount2 = StringUtils.containCount(sqlSnippet, Math.max(index1, index2), sqlSnippet.length(), quotations);
            if ((quotationCount1 + quotationCount2) % 2 != 0) {
                throw new SearcherException("There is a syntax error (quotations mismatch): " + sqlSnippet);
            }
            int nextIndex = index1 + sqlName.length();
            // 判断虚拟参数是否不在引号内部，并且不是以 :name: 的形式
            if (quotationCount1 % 2 == 0 && !endWithPrefix) {
                embedParam.setParameterized(true);
                sqlSnippet = sqlSnippet.replaceFirst(sqlName, "?");
                // sqlSnippet 长度变短，寻找下标也该相应提前
                nextIndex = nextIndex - sqlName.length() + 1;
            }
            solution.addEmbedParam(embedParam);
            index1 = sqlSnippet.indexOf(paramPrefix, nextIndex);
        }
        solution.setSqlSnippet(sqlSnippet);
        return solution;
    }

    private String getSqlName(String sqlSnippet, int index1, int index2) {
        if (index2 > 0) {
            return sqlSnippet.substring(index1, index2);
        } else {
            return sqlSnippet.substring(index1);
        }
    }

    private int findParamEndIndex(String sqlSnippet, int fromIndex) {
        int index = -1;
        for (String flag : paramEndFlags) {
            int index0 = sqlSnippet.indexOf(flag, fromIndex);
            if (index < 0) {
                index = index0;
            } else if (index0 > 0) {
                index = Math.min(index, index0);
            }
        }
        return index;
    }

    public String getParamPrefix() {
        return paramPrefix;
    }

    public void setParamPrefix(String paramPrefix) {
        this.paramPrefix = paramPrefix;
    }

    public String[] getParamEndFlags() {
        return paramEndFlags;
    }

    public void setParamEndFlags(String[] paramEndFlags) {
        this.paramEndFlags = paramEndFlags;
    }

    public char[] getQuotations() {
        return quotations;
    }

}
