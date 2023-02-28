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

/**
 * 默认 SQL 解析器
 *
 * @author Troy.Zhou @ 2023-03-20
 * @since v4.1.0
 */
public class GroupPairResolver implements GroupPair.Resolver {

    @Override
    public GroupPair resolve(BeanMeta<?> beanMeta, Group<List<FieldParam>> paramsGroup, String groupBy) {
        if (paramsGroup.isRaw()) {
            // 没有或只有且 的 原始组
            return splitRawParams(beanMeta, paramsGroup.getValue(), groupBy);
        }
        if (paramsGroup.judgeAll(list -> {
            for (FieldParam param: list) {
                if (isClusterField(beanMeta, param, groupBy)) {
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
                if (isClusterField(beanMeta, param, groupBy)) {
                    return true;
                }
            }
            return false;
        })) {
            // 如果所有条件字段都是 聚合字段，则都作为 having 条件
            return new GroupPair(GroupPair.EMPTY_GROUP, paramsGroup);
        }
        // 既包含 聚合字段，又包含 非聚合字段，只能进行拆分了
        return splitComplexGroup(beanMeta, paramsGroup, groupBy);
    }


    protected GroupPair splitComplexGroup(BeanMeta<?> beanMeta, Group<List<FieldParam>> paramsGroup, String groupBy) {
        // TODO:
        return new GroupPair(GroupPair.EMPTY_GROUP, paramsGroup);
    }


    protected GroupPair splitRawParams(BeanMeta<?> beanMeta, List<FieldParam> params, String groupBy) {
        List<FieldParam> where = new ArrayList<>();
        List<FieldParam> having = new ArrayList<>();
        for (FieldParam param: params) {
            if (isClusterField(beanMeta, param, groupBy)) {
                having.add(param);
            } else {
                where.add(param);
            }
        }
        return new GroupPair(new Group<>(where), new Group<>(having));
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
        return meta.getField() != null && !StringUtils.sqlContains(groupBy, meta.getFieldSql().getSql());
    }

}
