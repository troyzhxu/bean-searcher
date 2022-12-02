package cn.zhxu.bs.util;

/**
 * 缓存
 * @param <T> 泛型
 * @since v3.8.1
 * @author Troy.Zhou @ 2017-03-20
 */
public interface Cache<T> {

    /**
     * 获取缓存
     * @param key 键
     * @return 缓存值
     */
    T get(String key);

    /**
     * 投放缓存
     * @param key 键
     * @param value 缓存值
     */
    void cache(String key, T value);

}
