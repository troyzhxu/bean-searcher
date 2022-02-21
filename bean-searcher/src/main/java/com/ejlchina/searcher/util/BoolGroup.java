package com.ejlchina.searcher.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 逻辑组
 * @author Troy.Zhou @ 2022-02-21
 */
public class BoolGroup<Value> {

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
    private final List<BoolGroup<Value>> groups;

    // 该组的值（原生组才有 该值）
    private final Value value;

    public BoolGroup(int type) {
        this(type, Collections.emptyList());
    }

    public BoolGroup(int type, List<BoolGroup<Value>> groups) {
        this(type, groups, null);
    }

    public BoolGroup(Value value) {
        this(TYPE_RAW, Collections.emptyList(), value);
    }

    private BoolGroup(int type, List<BoolGroup<Value>> groups, Value value) {
        this.type = type;
        this.groups = groups;
        this.value = value;
    }

    public <R> BoolGroup<R> transform(Function<Value, R> transformer) {
        if (type == TYPE_RAW) {
            return new BoolGroup<>(transformer.apply(value));
        }
        List<BoolGroup<R>> newGroups = groups.stream()
                .map(g -> g.transform(transformer))
                .collect(Collectors.toList());
        return new BoolGroup<>(type, newGroups);
    }


    /**
     * 与另一个 BoolGroup 做 布尔运算
     * @param opType 运算符 TYPE_AND 或 TYPE_OR
     * @param other 另一个 BoolGroup
     * @return 运算结果
     */
    public BoolGroup<Value> boolWith(int opType, BoolGroup<Value> other) {
        int otherType = other.getType();
        if (type == opType) {
            List<BoolGroup<Value>> groups = new ArrayList<>(this.groups);
            if (otherType == opType) {
                groups.addAll(other.getGroups());
            } else {
                groups.add(other);
            }
            return new BoolGroup<>(opType, groups);
        }
        if (otherType == opType) {
            List<BoolGroup<Value>> groups = new ArrayList<>(other.getGroups());
            groups.add(this);
            return new BoolGroup<>(opType, groups);
        }
        return new BoolGroup<>(opType, Arrays.asList(this, other));
    }

    public int getType() {
        return type;
    }

    public List<BoolGroup<Value>> getGroups() {
        return groups;
    }

    public Value getValue() {
        return value;
    }

}
