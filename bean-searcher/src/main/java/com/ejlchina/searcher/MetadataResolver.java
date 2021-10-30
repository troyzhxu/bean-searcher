package com.ejlchina.searcher;

/***
 * Bean 的元信息 解析接口
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.0.0
 * */
public interface MetadataResolver {

    /**
     * @param beanClass 要检索的 bean 类型
     * @return Bean 的元信息
     * */
    <T> Metadata<T> resolve(Class<T> beanClass);

}
