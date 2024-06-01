package cn.zhxu.bs.group;

import cn.zhxu.bs.util.StringUtils;

import java.util.Stack;

/**
 * Group 表达式 解析器
 * @author Troy.Zhou @ 2022-02-21
 * @since v3.5.0
 */
public class DefaultExprParser implements ExprParser {

    // 表达式
    private final String expression;
    // 操作数栈
    private final Stack<Group<String>> valueStack = new Stack<>();
    // 运算符栈
    private final Stack<Character> opStack = new Stack<>();

    private int index = 0;      // 下一步该读取的下标

    public DefaultExprParser(String expression) {
        this.expression = expression;
    }

    @Override
    public Group<String> parse() {
        // a|b&(c|d|e)&d|f
        Object res = readNext();
        while (res != null) {
            if (res instanceof String) {
                String value = (String) res;
                if (StringUtils.isNotBlank(value)) {
                    valueStack.push(new Group<>(value));
                }
            } else if (res instanceof Character) {
                // 读取到运算符，调用单独的方法处理
                onReadOperator((Character) res);
            }
            res = readNext();
        }
        return getResult();
    }

    /**
     * 读取下一个
     * @return 运算符 或 操作数
     */
    protected Object readNext() {
        int initIndex = index;
        while (index < expression.length()) {
            char ch = expression.charAt(index);
            if (ch == AND_OP || ch == OR_OP || ch == BRACKET_LEFT || ch == BRACKET_RIGHT) {
                if (index == initIndex) {
                    index++;
                    return ch;
                }
                return expression.substring(initIndex, index).trim();
            }
            index++;
        }
        if (index > initIndex) {
            return expression.substring(initIndex, index).trim();
        }
        return null;
    }

    protected void onReadOperator(char op) {
        if (op != BRACKET_LEFT) {
            while (!opStack.isEmpty()) {
                // 取出栈顶运算符
                char topOp = opStack.pop();
                if (op == BRACKET_RIGHT) {
                    if (topOp == BRACKET_LEFT) {
                        return;
                    }
                } else if (topOp == BRACKET_LEFT || op == AND_OP && topOp == OR_OP) {
                    // 新的运算符优先级高，则栈顶运算符归位并退出循环
                    opStack.push(topOp);
                    break;
                }
                // 更新操作数栈
                updateValueStack(topOp);
            }
        }
        // 根据规则（2）运算符栈为空 或 新的运算符优先级高
        opStack.push(op);
    }

    protected void updateValueStack(char op) {
        // 弹出两个操作数进行计算
        Group<String> value2 = valueStack.pop();
        Group<String> value1 = valueStack.pop();
        // 计算结果压入操作数栈
        if (op == AND_OP) {
            valueStack.push(value1.and(value2));
        } else if (op == OR_OP) {
            valueStack.push(value1.or(value2));
        } else {
            throw new IllegalStateException("Invalid groupExpr: " + expression);
        }
    }

    protected Group<String> getResult() {
        while (!opStack.isEmpty()) {
            char op = opStack.pop();
            updateValueStack(op);
        }
        return valueStack.pop();
    }

}
