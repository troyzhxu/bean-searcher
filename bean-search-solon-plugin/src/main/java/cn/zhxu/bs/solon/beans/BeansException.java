package cn.zhxu.bs.solon.beans;

import org.noear.solon.lang.Nullable;

/**
 * @author noear 2023/2/18 created
 */
public abstract class BeansException extends RuntimeException {
    public BeansException(String msg) {
        super(msg);
    }

    public BeansException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

