package cn.zhxu.bs;

/**
 * Bean 的元信息 解析接口
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.0.0
 * */
public interface MetaResolver {

    /**
     * @param <T> 泛型
     * @param beanClass 要检索的 bean 类型
     * @return Bean 的元信息
     * */
    <T> BeanMeta<T> resolve(Class<T> beanClass);

    /**
     * 清除缓存
     * @since v4.4.0
     */
    void clearCache();

}
