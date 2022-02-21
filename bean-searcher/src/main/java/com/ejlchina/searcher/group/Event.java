package com.ejlchina.searcher.group;

/**
 * Group 表达式 解析器
 * @author Troy.Zhou @ 2022-02-21
 * @since v3.5.0
 */
public class Event<Value> {

    public static final int TYPE_VALUE = 1;
    public static final int TYPE_GROUP_START = 2;
    public static final int TYPE_GROUP_END = 3;
    public static final int TYPE_AND = 4;
    public static final int TYPE_OR = 5;

    private final int type;
    private final Value value;

    public Event(int type) {
        this(type, null);
    }

    public Event(int type, Value value) {
        this.type = type;
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public boolean isValue() {
        return type == TYPE_VALUE;
    }

    public boolean isGroupStart() {
        return type == TYPE_GROUP_START;
    }

    public boolean isGroupEnd() {
        return type == TYPE_GROUP_START;
    }

    public boolean isGroupAnd() {
        return type == TYPE_AND;
    }

    public boolean isGroupOr() {
        return type == TYPE_OR;
    }

}
