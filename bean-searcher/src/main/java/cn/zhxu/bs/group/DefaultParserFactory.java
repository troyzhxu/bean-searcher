package cn.zhxu.bs.group;

/**
 * ExprParser 工厂实现
 * @author Troy.Zhou @ 2022-02-22
 * @since v3.5.0
 */
public class DefaultParserFactory implements ExprParser.Factory {

    @Override
    public ExprParser create(String expr) {
        return new DefaultExprParser(expr);
    }

    @Override
    public char getAndKey() {
        return ExprParser.AND_OP;
    }

    @Override
    public char getOrKey() {
        return ExprParser.OR_OP;
    }

}
