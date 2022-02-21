package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.util.GroupExpr;
import com.ejlchina.searcher.util.StringUtils;

import java.util.Stack;

/**
 * GroupExpr 解析器
 * @author Troy.Zhou @ 2022-02-21
 * @since v3.5.0
 */
public class GroupExprParser {

    // 表达式
    private final String expression;
    // 操作数栈
    private final Stack<GroupExpr<String>> valueStack = new Stack<>();
    // 运算符栈
    private final Stack<Character> opStack = new Stack<>();

    private final char andOp;

    private final char orOp;

    private int index;      // 下一步该读取的下标

    public GroupExprParser(String expression, char andOp, char orOp) {
        this.expression = expression;
        this.andOp = andOp;
        this.orOp = orOp;
    }

    // a+b*(c+d+e)*d+f
    public GroupExpr<String> parse() {
        Object res = readNext();
        while (res != null) {
            if (res instanceof String) {
                String value = (String) res;
                if (StringUtils.isNotBlank(value)) {
                    valueStack.push(new GroupExpr<>(value));
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
        int valueEnd = 0;
        int initIndex = index;
        while (index < expression.length()) {
            char ch = expression.charAt(index);
            if (ch == andOp || ch == orOp || ch == '(' || ch == ')') {
                if (valueEnd == initIndex) {
                    index++;
                    return ch;
                }
                return expression.substring(initIndex, valueEnd).trim();
            } else {
                valueEnd++;
            }
            index++;
        }
        if (valueEnd > initIndex) {
            return expression.substring(initIndex, valueEnd).trim();
        }
        return null;
    }

    protected void onReadOperator(char op) {
        if (op != '(') {
            while (opStack.size() > 0) {
                // 取出栈顶运算符
                char topOp = opStack.pop();
                if (op == ')') {
                    if (topOp == '(') {
                        return;
                    }
                } else if (topOp == '(' || op == andOp && topOp == orOp) {
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
        GroupExpr<String> value2 = valueStack.pop();
        GroupExpr<String> value1 = valueStack.pop();
        // 计算结果压入操作数栈
        valueStack.push(value1.boolWith(op, value2));
    }

    protected GroupExpr<String> getResult() {
        while (opStack.size() > 0) {
            char op = opStack.pop();
            updateValueStack(op);
        }
        return valueStack.pop();
    }

}
