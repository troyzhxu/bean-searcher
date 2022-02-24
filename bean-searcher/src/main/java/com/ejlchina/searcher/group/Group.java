package com.ejlchina.searcher.group;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 逻辑组
 * @author Troy.Zhou @ 2022-02-21
 * @since v3.5.0
 */
public class Group<V> {

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
    private final List<Group<V>> groups;

    // 该组的值（原生组才有 该值）
    private final V value;

    public Group(int type) {
        this(type, Collections.emptyList());
    }

    public Group(int type, List<Group<V>> groups) {
        this(type, groups, null);
    }

    public Group(V value) {
        this(TYPE_RAW, Collections.emptyList(), value);
    }

    private Group(int type, List<Group<V>> groups, V value) {
        this.type = type;
        this.groups = groups;
        this.value = value;
    }

    /**
     * 值转换
     * @param transformer 转换器
     * @param <R> 转换后的类型
     * @return Group
     */
    public <R> Group<R> transform(Function<V, R> transformer) {
        if (type == TYPE_RAW) {
            return new Group<>(transformer.apply(value));
        }
        List<Group<R>> newGroups = groups.stream()
                .map(g -> g.transform(transformer))
                .collect(Collectors.toList());
        return new Group<>(type, newGroups);
    }

    /**
     * 过滤不必要的子组
     * @param predicate 判断器
     * @return 过滤后的 Group
     */
    public Group<V> filter(Predicate<V> predicate) {
        if (type == TYPE_RAW) {
            return this;
        }
        List<Group<V>> newGroups = new ArrayList<>();
        for (Group<V> group: groups) {
            if (group.type == TYPE_RAW) {
                if (predicate.test(group.value)) {
                    newGroups.add(group);
                }
                continue;
            }
            Group<V> newGroup = group.filter(predicate);
            if (newGroup.groups.size() > 0) {
                newGroups.add(newGroup);
            }
        }
        return new Group<>(type, newGroups);
    }


    /**
     * 判断是否存在一个 V 满足 evaluator
     * @param evaluator 评估器
     * @return boolean
     */
    public boolean judgeAny(Predicate<V> evaluator) {
        if (type == TYPE_RAW) {
            return evaluator.test(value);
        }
        for (Group<V> group: groups) {
            if (group.judgeAny(evaluator)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    private static final Event EVENT_START = new Event<>(Event.TYPE_GROUP_START);

    @SuppressWarnings("rawtypes")
    private static final Event EVENT_END = new Event<>(Event.TYPE_GROUP_END);

    @SuppressWarnings("rawtypes")
    private static final Event EVENT_AND = new Event<>(Event.TYPE_AND);

    @SuppressWarnings("rawtypes")
    private static final Event EVENT_OR = new Event<>(Event.TYPE_OR);

    /**
     * 遍历所有
     * @param consumer 消费者
     */
    @SuppressWarnings("unchecked")
    public void readAll(Consumer<Event<V>> consumer) {
        if (type == TYPE_RAW) {
            consumer.accept(new Event<>(Event.TYPE_VALUE, value));
            return;
        }
        if (groups.isEmpty()) {
            return;
        }
        consumer.accept(EVENT_START);
        for (int i = 0; i < groups.size(); i++) {
            if (i > 0) {
                consumer.accept(type == TYPE_AND ? EVENT_AND : EVENT_OR);
            }
            groups.get(i).readAll(consumer);
        }
        consumer.accept(EVENT_END);
    }

    /**
     * 计算组的复杂度
     * @return 复杂度
     */
    public int complexity() {
        if (type == TYPE_RAW) {
            return 1;
        }
        int sum = 0;
        for (Group<V> group: groups) {
            sum += group.complexity();
        }
        return sum;
    }

    /**
     * 与另一个 Group 做 且 运算
     * @param other 另一个 Group
     * @return 运算结果
     */
    public Group<V> and(Group<V> other) {
        return boolWith(TYPE_AND, other);
    }

    /**
     * 与另一个 Group 做 或 运算
     * @param other 另一个 Group
     * @return 运算结果
     */
    public Group<V> or(Group<V> other) {
        return boolWith(TYPE_OR, other);
    }

    /**
     * 与另一个 Group 进行逻辑运算，并会自动简化表达式
     * 简化依据为以下 5 组逻辑关系：
     *   (1)、A | A             = A                 A & A             = A
     *   (2)、A | (A & B)       = A                 A & (A | B)       = A
     *   (3)、A | ((A | C) & B) = A | (B & C)       A & ((A & C) | B) = A & (B | C)
     *   (4)、A | (B | C)       = A | B | C         A & (B & C)       = A & B & C
     *   (5)、若 A | B = A，则 A | B | C = A | C ;   若 A & B = A，则 A & B & C = A & C ;
     * @param opType 运算类型
     * @param other 另一个 Group
     * @return Group
     */
    private Group<V> boolWith(int opType, Group<V> other) {
        if (equals(other)) {
            return this;                // 化简：根据逻辑等式 (1)
        }
        Group<V> res = simplyBool(this, other, opType);
        if (res != null) {
            return res;
        }
        res = simplyBool(other, this, opType);
        if (res != null) {
            return res;
        }
        if (type == opType) {
            return sameBool(other);
        }
        if (other.type == opType) {
            return other.sameBool(this);
        }
        return new Group<>(opType, Arrays.asList(this, other));
    }

    /**
     * 同类运算
     * 当前是什么类型的组，就与 另一个 Group 进行什么类型的运算
     * @param other 另一个 Group
     * @return Group
     */
    private Group<V> sameBool(Group<V> other) {
        if (type == TYPE_RAW) {
            throw new IllegalStateException("raw group can not invoke this method: " + this);
        }
        List<Group<V>> groups = new ArrayList<>();
        for (Group<V> group: this.groups) {
            // 化简：根据逻辑关系 (5)
            Group<V> res = group.boolWith(type, other);
            if (res.equals(group)) {
                return this;
            }
            if (!other.equals(res)) {
                groups.add(group);
            }
        }
        if (groups.isEmpty()) {
            return other;
        }
        int oComplexity = other.complexity();
        for (Group<V> group: groups) {
            int gComplexity = group.complexity();
            Group<V> res = other.boolWith(type, group);
            if (res.complexity() < oComplexity + gComplexity) {
                groups.remove(group);
                groups.add(res);
                return new Group<>(type, groups);
            }
        }
        groups.add(other);
        return new Group<>(type, groups);
    }

    // 简化运算
    private static <V> Group<V> simplyBool(Group<V> g1, Group<V> g2, int opType) {
        if (g1.type != TYPE_RAW && g1.groups.size() == 1) {
            return g1.groups.get(0).boolWith(opType, g2);
        }
        if (g1.type != TYPE_RAW && g1.groups.contains(g2)) {
            // 根据逻辑等式 (2) / (1) (4)
            return g1.type == opType ? g1 : g2;
        }
        if (g1.type != TYPE_RAW && g1.type != opType) {
            for (Group<V> group: g1.groups) {
                if (group.type == opType) {
                    // 化简：根据逻辑等式 (3)
                    List<Group<V>> list = group.groups.stream()
                            .filter(g -> !g.equals(g2))
                            .collect(Collectors.toList());
                    if (list.size() < group.groups.size()) {
                        Group<V> newGroup = new Group<>(group.type, list);
                        for (Group<V> g: g1.groups) {
                            if (g != group) {
                                newGroup = newGroup.boolWith(g1.type, g);
                            }
                        }
                        return newGroup.boolWith(opType, g2);
                    }
                }
            }
        }
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group<?> other = (Group<?>) o;
        if (type != other.type) {
            return false;
        }
        return Objects.equals(groups, other.groups) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, groups, value);
    }

    @Override
    public String toString() {
        if (type == TYPE_AND) {
            return groups.stream().map(g -> {
                        if (g.type == TYPE_OR) {
                            return "(" + g + ")";
                        }
                        return g.toString();
                    })
                    .collect(Collectors.joining("&"));
        }
        if (type == TYPE_OR) {
            return groups.stream().map(Group::toString)
                    .collect(Collectors.joining("|"));
        }
        return value != null ? value.toString() : "";
    }

}
