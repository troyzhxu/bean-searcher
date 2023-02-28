package cn.zhxu.bs.implement;

import cn.zhxu.bs.BeanMeta;
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
    public GroupPair resolve(BeanMeta<?> beanMeta, Group<List<FieldParam>> paramsGroup) {
        List<String> selectFields = beanMeta.getSelectFields();
        String groupBy = beanMeta.getGroupBy();
        if (paramsGroup.isRaw()) {
            List<FieldParam> where = new ArrayList<>();
            List<FieldParam> having = new ArrayList<>();
            for (FieldParam param: paramsGroup.getValue()) {
                String name = param.getName();
                if (StringUtils.sqlContains(groupBy, beanMeta.getFieldSql(name))
                        || !selectFields.contains(name)) {
                    where.add(param);
                } else {
                    having.add(param);
                }
            }
            return new GroupPair(new Group<>(where), new Group<>(having));
        } else if (paramsGroup.judgeAll(list -> {
            for (FieldParam param: list) {
                String name = param.getName();
                if (!StringUtils.sqlContains(groupBy, beanMeta.getFieldSql(name))
                        && selectFields.contains(name)) {
                    return false;
                }
            }
            return true;
        })) {
            // v4.0.0: 如果所有条件字段都在 groupBy 里，则也把条件放在 where 里
            // v4.1.0: 如果所有条件字段都在 groupBy 或 @SearchBean.fields 里，则也把条件放在 where 里
            return new GroupPair(paramsGroup, GroupPair.EMPTY_GROUP);
        }
        // 复杂的组，都作为 having 条件
        return new GroupPair(GroupPair.EMPTY_GROUP, paramsGroup);
    }

}
