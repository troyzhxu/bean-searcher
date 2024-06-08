package cn.zhxu.bs.util;

import cn.zhxu.bs.ParamNames;

import java.util.Objects;

/**
 * 远程调用参数名称配置
 * @since v4.3
 */
public class RpcNames extends ParamNames<RpcNames> {

    /**
     * 默认配置
     */
    public static RpcNames DEFAULT = new RpcNames();

    /**
     * 新建配置
     * @return RpcNames
     */
    public static RpcNames newConfig() {
        return new RpcNames();
    }

    /**
     * 页码参数名
     */
    private String page = "page";

    /**
     * 偏移条数参数名
     */
    private String offset = "offset";

    /**
     * 分页尺寸参数名
     */
    private String size = "size";


    public String page() {
        return page;
    }

    public RpcNames page(String page) {
        this.page = Objects.requireNonNull(page);
        return this;
    }

    public String offset() {
        return offset;
    }

    public RpcNames offset(String offset) {
        this.offset = Objects.requireNonNull(offset);
        return this;
    }

    public String size() {
        return size;
    }

    public RpcNames size(String size) {
        this.size = Objects.requireNonNull(size);
        return this;
    }

}
