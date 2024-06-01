package cn.zhxu.bs.group;

/**
 * Group 表达式 解析器
 * @author Troy.Zhou @ 2022-02-21
 * @since v3.5.0
 */
public interface ExprParser {

    /**
     * 且关系运算符常量
     * @since v4.3.0
     */
    char AND_OP = '&';

    /**
     * 或关系关系符常量
     * @since v4.3.0
     */
    char OR_OP = '|';

    /**
     * 左括号
     * @since v4.3.0
     */
    char BRACKET_LEFT = '(';

    /**
     * 右号
     * @since v4.3.0
     */
    char BRACKET_RIGHT = ')';

    /**
     * 解析
     * @return Group
     */
    Group<String> parse();

    /**
     * 工厂
     */
    interface Factory {

        /**
         * 创建一个解析器
         * @param expr 表达式
         * @return ExprParser
         */
        ExprParser create(String expr);

        /**
         * deprecated from v4.3.0
         * @since v3.8.0
         * @return 且逻辑符
         * @see #AND_OP
         */
        @Deprecated
        default char getAndKey() {
            return AND_OP;
        }

        /**
         * deprecated from v4.3.0
         * @since v3.8.0
         * @return 或逻辑符
         * @see #OR_OP
         */
        @Deprecated
        default char getOrKey() {
            return OR_OP;
        }

    }

}
