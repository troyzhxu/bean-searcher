package cn.zhxu.bs;

import cn.zhxu.bs.param.FetchType;

import java.util.Map;

/**
 * 检索结果过滤器
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.6.0
 */
public interface ResultFilter {

    /**
     * ResultFilter
     * 对 {@link BeanSearcher } 的检索结果做进一步转换处理
     * @param result 检索结果
     * @param beanMeta 检索实体类的元信息
     * @param paraMap 检索参数
     * @param fetchType 检索类型
     * @param <T> 泛型
     * @return 转换后的检索结果
     */
    default <T> SearchResult<T> doBeanFilter(SearchResult<T> result, BeanMeta<T> beanMeta, Map<String, Object> paraMap, FetchType fetchType) {
        return result;
    }

    /**
     * 对 {@link MapSearcher } 的检索结果做进一步转换处理
     * @param result 检索结果
     * @param beanMeta 检索实体类的元信息
     * @param paraMap 检索参数
     * @param fetchType 检索类型
     * @param <T> 泛型
     * @return 转换后的检索结果
     */
    default <T> SearchResult<Map<String, Object>> doMapFilter(SearchResult<Map<String, Object>> result, BeanMeta<T> beanMeta, Map<String, Object> paraMap, FetchType fetchType) {
        return result;
    }

}
