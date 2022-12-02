package cn.zhxu.bs;

/**
 * SQL 片段解析器
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.0.0
 */
public interface SnippetResolver {

    /**
     * @param fragment SQL 碎片（非空）
     * @return 解析结果
     */
    SqlSnippet resolve(String fragment);

}
