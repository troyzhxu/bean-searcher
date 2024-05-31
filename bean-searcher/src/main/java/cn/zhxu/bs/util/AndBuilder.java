package cn.zhxu.bs.util;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * 并且关系参数构建器
 * @author Troy.Zhou @ 2024-06-01
 * @since v4.3.0
 */
public class AndBuilder extends Builder<AndBuilder> {

    private final String parentExpr;
    private String groupExpr;

    public AndBuilder(String parentExpr, String lastGroup) {
        super(new HashMap<>());
        this.parentExpr = parentExpr;
        this.group = nextGroup(lastGroup);
        this.groupExpr = group;
    }

    @Override
    protected boolean isGroupExists(String group) {
        return groupExpr != null && groupExpr.contains(group) || parentExpr != null && parentExpr.contains(group);
    }

    /**
     * 用于构建一组以或为关系的条件，例如：
     * <pre>{@code
     * Map<String, Object> params = MapUtils.builder()
     *     .field(User::getAge, 20).op(Equal.class);
     *     .or(b -> {
     *         b.field(User::getName, '张').op(StartWith.class);
     *         b.field(User::getName, '三').op(EndWith.class);
     *     })
     *     .build();
     * // 生成条件：(age = 20) and (name like '张%' or name like '三%')
     * }</pre>
     * @param condition 或条件构建器
     * @return AndBuilder
     * @since v4.3.0
     */
    public AndBuilder or(Consumer<OrBuilder> condition) {
        return withOr(condition, parentExpr + groupExpr);
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
