package cn.zhxu.bs.util;

import java.util.HashMap;
import java.util.function.Consumer;

import static cn.zhxu.bs.group.ExprParser.*;

/**
 * 或者关系参数构建器
 * @author Troy.Zhou @ 2024-06-01
 * @since v4.3.0
 */
public class OrBuilder extends Builder<OrBuilder> {

    private final String parentExpr;
    private String groupExpr;

    public OrBuilder(String parentExpr, String lastGroup) {
        super(new HashMap<>());
        this.parentExpr = parentExpr;
        group = lastGroup;
    }

    @Override
    public OrBuilder field(String fieldName, Object... values) {
        group = nextGroup(group);
        if (groupExpr != null) {
            groupExpr += OR_OP + group;
        } else {
            groupExpr = group;
        }
        return super.field(fieldName, values);
    }

    @Override
    protected boolean isGroupExists(String group) {
        return groupExpr != null && groupExpr.contains(group) || parentExpr != null && parentExpr.contains(group);
    }

    /**
     * 用于构建一组以且为关系的条件，例如：
     * <pre>{@code
     * Map<String, Object> params = MapUtils.builder()
     *     .or(o -> {
     *         o.field(User::getAge, 20).op(Equal.class);
     *         o.and(a -> {
     *             a.field(User::getName, '张').op(StartWith.class);
     *             a.field(User::getName, '三').op(EndWith.class);
     *         })
     *     })
     *     .build();
     * // 生成条件：(age = 20) or (name like '张%' and name like '三%')
     * }</pre>
     * @param condition 或条件构建器
     * @return MapBuilder
     * @since v4.3.0
     */
    public OrBuilder and(Consumer<AndBuilder> condition) {
        AndBuilder builder = new AndBuilder(parentExpr + groupExpr, group);
        condition.accept(builder);
        String expr1 = builder.getGroupExpr();
        if (expr1 != null) {
            String expr0 = getGroupExpr();
            if (expr0 != null) {
                setGroupExpr(BRACKET_LEFT + expr0 + BRACKET_RIGHT + OR_OP + BRACKET_LEFT + expr1 + BRACKET_RIGHT);
            } else {
                setGroupExpr(BRACKET_LEFT + expr1 + BRACKET_RIGHT);
            }
        }
        map.putAll(builder.map);
        return this;
    }

    @Override
    protected void setGroupExpr(String groupExpr) {
        this.groupExpr = groupExpr;
    }

    @Override
    protected String getGroupExpr() {
        return groupExpr;
    }

}
