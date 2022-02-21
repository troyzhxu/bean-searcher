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
public class Group<Value> {

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
    private final List<Group<Value>> groups;

    // 该组的值（原生组才有 该值）
    private final Value value;

    public Group(int type) {
        this(type, Collections.emptyList());
    }

    public Group(int type, List<Group<Value>> groups) {
        this(type, groups, null);
    }

    public Group(Value value) {
        this(TYPE_RAW, Collections.emptyList(), value);
    }

    private Group(int type, List<Group<Value>> groups, Value value) {
        this.type = type;
        this.groups = groups;
        this.value = value;
    }

    /**
     * 值转换
     * @param transformer 转换器
     * @param <R> 转换后的类型
     * @return Group<R>
     */
    public <R> Group<R> transform(Function<Value, R> transformer) {
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
    public Group<Value> filter(Predicate<Value> predicate) {
        if (type == TYPE_RAW) {
            return this;
        }
        List<Group<Value>> newGroups = new ArrayList<>();
        for (Group<Value> group: groups) {
            if (group.type == TYPE_RAW) {
                if (predicate.test(group.value)) {
                    newGroups.add(group);
                }
                continue;
            }
            Group<Value> newGroup = group.filter(predicate);
            if (newGroup.groups.size() > 0) {
                newGroups.add(newGroup);
            }
        }
        return new Group<>(type, newGroups);
    }


    /**
     * 判断是否存在一个 Value 满足 evaluator
     * @param evaluator 评估器
     * @return boolean
     */
    public boolean judgeAny(Predicate<Value> evaluator) {
        if (type == TYPE_RAW) {
            return evaluator.test(value);
        }
        for (Group<Value> group: groups) {
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
    public void readAll(Consumer<Event<Value>> consumer) {
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
                if (type == TYPE_AND) {
                    consumer.accept(EVENT_AND);
                }
                if (type == TYPE_OR) {
                    consumer.accept(EVENT_OR);
                }
            }
            groups.get(i).readAll(consumer);
        }
        consumer.accept(EVENT_END);
    }

    /**
     * 与另一个 Group 做 且 运算
     * @param other 另一个 Group
     * @return 运算结果
     */
    public Group<Value> and(Group<Value> other) {
        return boolWith(TYPE_AND, other);
    }

    /**
     * 与另一个 Group 做 或 运算
     * @param other 另一个 Group
     * @return 运算结果
     */
    public Group<Value> or(Group<Value> other) {
        return boolWith(TYPE_OR, other);
    }

    private Group<Value> boolWith(int opType, Group<Value> other) {
        if (equals(other)) {
            return this;
        }
        int otherType = other.getType();
        if (type == opType) {
            List<Group<Value>> groups = new ArrayList<>(this.groups);
            if (otherType == opType) {
                groups.addAll(other.getGroups());
            } else {
                groups.add(other);
            }
            return new Group<>(opType, groups.stream().distinct().collect(Collectors.toList()));
        }
        if (otherType == opType) {
            List<Group<Value>> groups = new ArrayList<>(other.getGroups());
            if (!groups.contains(this)) {
                groups.add(this);
            }
            return new Group<>(opType, groups);
        }
        return new Group<>(opType, Arrays.asList(this, other));
    }

    public int getType() {
        return type;
    }

    public List<Group<Value>> getGroups() {
        return groups;
    }

    public Value getValue() {
        return value;
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
                    .collect(Collectors.joining("+"));
        }
        if (type == TYPE_OR) {
            return groups.stream().map(Group::toString)
                    .collect(Collectors.joining("|"));
        }
        return value != null ? value.toString() : "";
    }

}
