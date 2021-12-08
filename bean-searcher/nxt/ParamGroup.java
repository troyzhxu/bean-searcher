package com.ejlchina.searcher.param;

import java.util.List;

/**
 * 字段参数组
 * @author Troy.Zhou @ 2021-10-31
 * @since v3.3.0
 */
public class ParamGroup {

    /**
     * 且组，表示子组 groups 之间都是 且 的关系
     */
    public static final int TYPE_AND = 1;
    /**
     * 或组，表示子组 groups 之间都是 或 的关系
     */
    public static final int TYPE_OR = 2;
    /**
     * 原生组，表示改组有原生 params 参数集
     */
    public static final int TYPE_RAW = 3;

    // 类型
    private int type;
    // 子组：且组 与 或组 有此属性
    private List<ParamGroup> groups;
    // 原生组有此属性
    private List<FieldParam> params;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<ParamGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ParamGroup> groups) {
        this.groups = groups;
    }

    public List<FieldParam> getParams() {
        return params;
    }

    public void setParams(List<FieldParam> params) {
        this.params = params;
    }

}
