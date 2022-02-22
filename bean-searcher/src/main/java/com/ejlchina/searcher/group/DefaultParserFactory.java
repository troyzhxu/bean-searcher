package com.ejlchina.searcher.group;

/**
 * ExprParser 工厂实现
 * @author Troy.Zhou @ 2022-02-22
 * @since v3.5.0
 */
public class DefaultParserFactory implements ExprParser.Factory {

    private char andKey = '&';

    private char orKey = '|';

    @Override
    public ExprParser create(String expr) {
        return new DefaultExprParser(expr, andKey, orKey);
    }

    public char getAndKey() {
        return andKey;
    }

    public void setAndKey(char andKey) {
        this.andKey = andKey;
    }

    public char getOrKey() {
        return orKey;
    }

    public void setOrKey(char orKey) {
        this.orKey = orKey;
    }

}
