package com.ejlchina.searcher.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GroupExpr<Value> {

    /**
     * 且组，表示子组 groups 之间都是 且 的关系
     */
    public static final int TYPE_AND = 1;
    /**
     * 或组，表示子组 groups 之间都是 或 的关系
     */
    public static final int TYPE_OR = 2;
    /**
     * 原生组，表示改组有原生参数集
     */
    public static final int TYPE_RAW = 3;

    // 组类型
    private final int type;

    // 子组：且组 与 或组 有此属性
    private final List<GroupExpr<Value>> groups;

    // 该组的值
    private final Value value;

    public GroupExpr(int type) {
        this(type, Collections.emptyList());
    }

    public GroupExpr(int type, List<GroupExpr<Value>> groups) {
        this(type, groups, null);
    }

    public GroupExpr(Value value) {
        this(TYPE_RAW, Collections.emptyList(), value);
    }

    private GroupExpr(int type, List<GroupExpr<Value>> groups, Value value) {
        this.type = type;
        this.groups = groups;
        this.value = value;
    }

    public <R> GroupExpr<R> transform(Function<Value, R> transformer) {
        if (type == TYPE_RAW) {
            return new GroupExpr<>(transformer.apply(value));
        }
        List<GroupExpr<R>> newGroups = groups.stream()
                .map(g -> g.transform(transformer))
                .collect(Collectors.toList());
        return new GroupExpr<>(type, newGroups);
    }


    /**
     * 与另一个 GroupExpr 做 布尔运算
     * @param opType 运算符 TYPE_AND 或 TYPE_OR
     * @param other 另一个 GroupExpr
     * @return 运算结果
     */
    public GroupExpr<Value> boolWith(int opType, GroupExpr<Value> other) {
        int otherType = other.getType();
        if (type == opType) {
            List<GroupExpr<Value>> groups = new ArrayList<>(this.groups);
            if (otherType == opType) {
                groups.addAll(other.getGroups());
            } else {
                groups.add(other);
            }
            return new GroupExpr<>(opType, groups);
        }
        if (otherType == opType) {
            List<GroupExpr<Value>> groups = new ArrayList<>(other.getGroups());
            groups.add(this);
            return new GroupExpr<>(opType, groups);
        }
        return new GroupExpr<>(opType, Arrays.asList(this, other));
    }

    public int getType() {
        return type;
    }

    public List<GroupExpr<Value>> getGroups() {
        return groups;
    }

    public Value getValue() {
        return value;
    }

}
