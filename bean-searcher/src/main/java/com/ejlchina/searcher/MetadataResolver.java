package com.ejlchina.searcher;

/***
 * @author Troy.Zhou @ 2021-10-30
 *
 * Bean 的元信息 解析接口
 *
 * */
public interface MetadataResolver {

    /**
     * @param beanClass 要检索的 bean 类型
     * @return Bean 的元信息
     * */
    Metadata resolve(Class<?> beanClass);

}
