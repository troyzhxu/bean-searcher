package com.ejlchina.searcher;

/**
 * 内嵌参数解析器
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.0.0
 */
public interface EmbedParamResolver {

    /**
     *
     * @param sqlSnippet SQL 片段
     * @return 解析结果
     */
    EmbedSolution resolve(String sqlSnippet);

}
