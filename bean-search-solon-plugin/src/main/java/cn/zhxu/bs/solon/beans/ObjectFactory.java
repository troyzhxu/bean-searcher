package cn.zhxu.bs.solon.beans;

/**
 * @author noear 2023/2/18 created
 */
@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject() throws BeansException;
}

