package com.ejlchina.searcher;

/**
 * @author Troy.Zhou @ 2021-10-30
 * 内嵌参数解析器
 */
public interface EmbedParamResolver {

    /**
     *
     * @param sqlSnippet SQL 片段
     * @return 解析结果
     */
    EmbedSolution resolve(String sqlSnippet);

}
