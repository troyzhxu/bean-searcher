package cn.zhxu.bs;

import cn.zhxu.bs.util.FieldFns;

import java.util.List;
import java.util.Map;

/**
 * Bean 对象检索器接口
 * 根据 SearchBean 的 Class 和 检索参数，自动检索，数据以 SearchBean 泛型对象呈现
 * @author Troy.Zhou @ 2021-10-29
 * @since v3.0.0
 * */
public interface BeanSearcher extends Searcher {

    /**
     * 返回满足条件的数据列表（分页）以及总条数（此方法省略了检索参数，您可以在参数过滤器中注入它们）
     * @param <T> bean 类型
     * @param beanClass 要检索的 bean 类型
     * @return { 总条数，数据列表 }
     * @since v4.0.0
     * */
    <T> SearchResult<T> search(Class<T> beanClass);

    /**
     * 返回满足条件的数据列表（分页）以及总条数。
     * @param <T> bean 类型
     * @param beanClass 要检索的 bean 类型
     * @param paraMap 检索参数
     * @return { 总条数，数据列表 }
     * */
    <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap);

    /**
     * 返回满足条件的数据列表（分页）、总条数 以及 指定字段的统计（此方法省略了检索参数，您可以在参数过滤器中注入它们）
     * @param <T> bean 类型
     * @param beanClass 要检索的 bean 类型
     * @param summaryField 统计字段
     * @return { 总条数，数据列表，统计和 }
     * @since v4.0.0
     * */
    <T> SearchResult<T> search(Class<T> beanClass, String summaryField);

    /**
     * 返回满足条件的数据列表（分页）、总条数 以及 指定字段的统计
     * @param <T> bean 类型
     * @param beanClass 要检索的 bean 类型
     * @param paraMap 检索参数
     * @param summaryField 统计字段
     * @return { 总条数，数据列表，统计和 }
     * @since v4.0.0
     * */
    <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap, String summaryField);

    /**
     * 返回满足条件的数据列表（分页）、总条数 以及 指定字段的统计（此方法省略了检索参数，您可以在参数过滤器中注入它们）
     * @param <T> bean 类型
     * @param beanClass 要检索的 bean 类型
     * @param summaryField 统计字段
     * @return { 总条数，数据列表，统计和 }
     * @since v4.0.0
     * */
    <T> SearchResult<T> search(Class<T> beanClass, FieldFns.FieldFn<T, ?> summaryField);

    /**
     * 返回满足条件的数据列表（分页）、总条数 以及 指定字段的统计
     * @param <T> bean 类型
     * @param beanClass 要检索的 bean 类型
     * @param paraMap 检索参数
     * @param summaryField 统计字段
     * @return { 总条数，数据列表，统计和 }
     * @since v4.0.0
     * */
    <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap, FieldFns.FieldFn<T, ?> summaryField);

    /**
     * 返回满足条件的数据列表（分页）、总条数 以及 指定字段的统计（此方法省略了检索参数，您可以在参数过滤器中注入它们）
     * @param <T> bean 类型
     * @param beanClass 要检索的 bean 类型
     * @param summaryFields 统计字段
     * @return { 总条数，数据列表，统计和 }
     * @since v4.0.0
     * */
    <T> SearchResult<T> search(Class<T> beanClass, String[] summaryFields);

    /**
     * 返回满足条件的数据列表（分页）、总条数 以及 指定字段的统计
     * @param <T> bean 类型
     * @param beanClass 要检索的 bean 类型
     * @param paraMap 检索参数
     * @param summaryFields 统计字段
     * @return { 总条数，数据列表，统计和 }
     * */
    <T> SearchResult<T> search(Class<T> beanClass, Map<String, Object> paraMap, String[] summaryFields);

    /**
     * 返回满足条件的第一条数据（支持分页偏移）（此方法省略了检索参数，您可以在参数过滤器中注入它们）
     * @param <T> bean 类型
     * @param beanClass 要检索的 bean 类型
     * @return 满足条件的第一条数据
     * @since v4.0.0
     * */
    <T> T searchFirst(Class<T> beanClass);

    /**
     * 返回满足条件的第一条数据（支持分页偏移）
     * @param <T> bean 类型
     * @param beanClass 要检索的 bean 类型
     * @param paraMap 检索参数（包括排序分页参数）
     * @return 满足条件的第一条数据 
     * */
    <T> T searchFirst(Class<T> beanClass, Map<String, Object> paraMap);

    /**
     * 返回满足条件的数据列表（分页）（此方法省略了检索参数，您可以在参数过滤器中注入它们）
     * @param <T> bean 类型
     * @param beanClass 要检索的 bean 类型
     * @return 数据列表
     * @since v4.0.0
     * */
    <T> List<T> searchList(Class<T> beanClass);

    /**
     * 返回满足条件的数据列表（分页）
     * @param <T> bean 类型
     * @param beanClass 要检索的 bean 类型
     * @param paraMap 检索参数（包括排序分页参数）
     * @return 数据列表
     * */
    <T> List<T> searchList(Class<T> beanClass, Map<String, Object> paraMap);

    /**
     * 返回满足条件的所有数据（不支持分页偏移）（此方法省略了检索参数，您可以在参数过滤器中注入它们）
     * @param <T> bean 类型
     * @param beanClass 要检索的 bean 类型
     * @return 数据列表
     * @since v4.0.0
     * */
    <T> List<T> searchAll(Class<T> beanClass);

    /**
     * 回满足条件的所有数据（不支持分页偏移）
     * @param <T> bean 类型
     * @param beanClass 要检索的 bean 类型
     * @param paraMap 检索参数（包括排序分页参数）
     * @return 数据列表
     * */
    <T> List<T> searchAll(Class<T> beanClass, Map<String, Object> paraMap);

}
