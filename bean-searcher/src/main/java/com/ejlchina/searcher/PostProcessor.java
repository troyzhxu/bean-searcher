package com.ejlchina.searcher;

import com.ejlchina.searcher.param.FetchType;

import java.util.Map;

/**
 * 后处理器
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.6.0
 */
public interface PostProcessor {

    /**
     * 对 {@link BeanSearcher } 的检索结果做进一步转换处理
     * @param beanMeta 检索实体类的元信息
     * @param fetchType 检索类型
     * @param paraMap 检索参数
     * @param result 检索结果
     * @param <T> 泛型
     * @return 转换后的检索结果
     */
    default <T> SearchResult<T> beanProcess(BeanMeta<T> beanMeta, FetchType fetchType, Map<String, Object> paraMap, SearchResult<T> result) {
        return result;
    }

    /**
     * 对 {@link MapSearcher } 的检索结果做进一步转换处理
     * @param beanMeta 检索实体类的元信息
     * @param fetchType 检索类型
     * @param paraMap 检索参数
     * @param result 检索结果
     * @param <T> 泛型
     * @return 转换后的检索结果
     */
    default <T> SearchResult<Map<String, Object>> mapProcess(BeanMeta<T> beanMeta, FetchType fetchType, Map<String, Object> paraMap, SearchResult<Map<String, Object>> result) {
        return result;
    }

}
