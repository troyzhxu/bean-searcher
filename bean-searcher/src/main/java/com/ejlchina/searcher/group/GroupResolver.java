package com.ejlchina.searcher.group;

/**
 * Group 解析器
 * @author Troy.Zhou @ 2022-02-22
 * @since v3.5.0
 */
public interface GroupResolver {

    /**
     * 将组表达式解析为 Group 对象
     * @param gExpr 组表达式
     * @return Group
     */
    Group<String> resolve(String gExpr);

    /**
     * @since v3.8.0
     * @return ExprParser.Factory
     */
    ExprParser.Factory getParserFactory();

}
