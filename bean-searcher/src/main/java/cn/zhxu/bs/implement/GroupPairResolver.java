package cn.zhxu.bs.implement;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.bean.Cluster;
import cn.zhxu.bs.group.Group;
import cn.zhxu.bs.group.GroupPair;
import cn.zhxu.bs.param.FieldParam;
import cn.zhxu.bs.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 默认 GroupPair 解析器
 *
 * @author Troy.Zhou @ 2023-03-01
 * @since v4.1.0
 */
public class GroupPairResolver implements GroupPair.Resolver {

    @Override
    public GroupPair resolve(BeanMeta<?> beanMeta, Group<List<FieldParam>> paramsGroup, String groupBy) {
        Predicate<FieldParam> havingTester = p -> isClusterField(beanMeta, p, groupBy);
        if (paramsGroup.isRaw()) {
            // 没有或只有且 的 原始组
            return buildGroupPair(paramsGroup.getValue(), havingTester);
        }
        if (paramsGroup.judgeAll(list -> {
            for (FieldParam param: list) {
                if (havingTester.test(param)) {
                    return false;
                }
            }
            return true;
        })) {
            // 如果所有条件字段都是 非聚合字段，则都作为 where 条件
            return new GroupPair(paramsGroup, GroupPair.EMPTY_GROUP);
        }
        if (paramsGroup.judgeAll(list -> {
            for (FieldParam param: list) {
                if (!havingTester.test(param)) {
                    return false;
                }
            }
            return true;
        })) {
            // 如果所有条件字段都是 聚合字段，则都作为 having 条件
            return new GroupPair(GroupPair.EMPTY_GROUP, paramsGroup);
        }
        /* 既包含 聚合字段，又包含 非聚合字段，只能进行拆分了
         * 但由于 where 与 having 在一个 SQL 中只能是 且 的关系，所以此处的拆分可能会该变 原有的 逻辑语义
         * 因此使用者应该自己保证 聚合字段条件 与 非聚合字段条件 不应该存在 或 的关系 */
        return buildGroupPair(paramsGroup, havingTester);
    }

    protected boolean isClusterField(BeanMeta<?> beanMeta, FieldParam param, String groupBy) {
        FieldMeta meta = beanMeta.requireFieldMeta(param.getName());
        Cluster cluster = meta.getCluster();
        if (cluster == Cluster.FALSE) {
            return false;
        }
        if (cluster == Cluster.TRUE) {
            return true;
        }
        // 自动推断 是否是 聚合字段
        return meta.selectable() && !StringUtils.sqlContains(groupBy, meta.getFieldSql().getSql());
    }

    protected GroupPair buildGroupPair(List<FieldParam> params, Predicate<FieldParam> havingTester) {
        List<FieldParam> where = new ArrayList<>();
        List<FieldParam> having = new ArrayList<>();
        for (FieldParam param: params) {
            if (havingTester.test(param)) {
                having.add(param);
            } else {
                where.add(param);
            }
        }
        return new GroupPair(new Group<>(where), new Group<>(having));
    }

    /**
     * 由于 where 与 having 只能是 且 的关系，所以此处的拆分可能会该变 原有的 逻辑语义
     * 因此使用者应该自己保证 聚合字段条件 与 非聚合字段条件 不应该存在 或 的关系
     * @param group 待拆分的组
     * @param havingTester 测试器
     * @return GroupPair
     */
    protected GroupPair buildGroupPair(Group<List<FieldParam>> group, Predicate<FieldParam> havingTester) {
        if (group == GroupPair.EMPTY_GROUP) {
            return GroupPair.EMPTY_PAIR;
        }
        if (group.isRaw()) {
            return buildGroupPair(group.getValue(), havingTester);
        }
        // 递归合并
        List<GroupPair> children = group.getGroups().stream()
                .map(g -> buildGroupPair(g, havingTester))
                .collect(Collectors.toList());
        if (children.isEmpty()) {
            return GroupPair.EMPTY_PAIR;
        }
        Group<List<FieldParam>> where = children.get(0).getWhereGroup();
        Group<List<FieldParam>> having = children.get(0).getHavingGroup();
        for (int i = 1; i < children.size(); i++) {
            GroupPair pair = children.get(i);
            if (pair == GroupPair.EMPTY_PAIR) {
                continue;
            }
            where = compute(group.isAnd(), where, pair.getWhereGroup());
            having = compute(group.isAnd(), having, pair.getHavingGroup());
        }
        return new GroupPair(where, having);
    }

    protected Group<List<FieldParam>> compute(boolean isAnd, Group<List<FieldParam>> group, Group<List<FieldParam>> other) {
        if (group == GroupPair.EMPTY_GROUP) {
            group = other;
        } else if (other != GroupPair.EMPTY_GROUP) {
            if (isAnd) {
                group = group.and(other);
            } else {
                group = group.or(other);
            }
        }
        return group;
    }

}
