package cn.zhxu.bs.ex;

import java.io.IOException;
import java.util.Map;

/**
 * Bean 导出器（导出成 CSV 文件）
 * @author Troy.Zhou @ 2025-08-28
 * @since v4.5.0
 */
public interface BeanExporter {

    /**
     * @param name 导出文件的文件名
     * @param beanClass 数据类
     * @param <T> 数据类泛型
     * @throws IOException 抛出 IO 异常
     */
    <T> void export(String name, Class<T> beanClass) throws IOException;

    /**
     * @param name 导出文件的文件名
     * @param beanClass 数据类
     * @param batchSize 每次查询的数据条数
     * @param <T> 数据类泛型
     * @throws IOException 抛出 IO 异常
     */
    <T> void export(String name, Class<T> beanClass, int batchSize) throws IOException;

    /**
     * @param name 导出文件的文件名
     * @param beanClass 数据类
     * @param paraMap 额外查询参数
     * @param <T> 数据类泛型
     * @throws IOException 抛出 IO 异常
     */
    <T> void export(String name, Class<T> beanClass, Map<String, Object> paraMap) throws IOException;

    /**
     * @param name 导出文件的文件名
     * @param beanClass 数据类
     * @param paraMap 额外查询参数
     * @param batchSize 每次查询的数据条数
     * @param <T> 数据类泛型
     * @throws IOException 抛出 IO 异常
     */
    <T> void export(String name, Class<T> beanClass, Map<String, Object> paraMap, int batchSize) throws IOException;

    /**
     * @param writer 文件写入器
     * @param beanClass 数据类
     * @param <T> 数据类泛型
     * @throws IOException 抛出 IO 异常
     */
    <T> void export(FileWriter writer, Class<T> beanClass) throws IOException;

    /**
     * @param writer 文件写入器
     * @param beanClass 数据类
     * @param batchSize 每次查询的数据条数
     * @param <T> 数据类泛型
     * @throws IOException 抛出 IO 异常
     */
    <T> void export(FileWriter writer, Class<T> beanClass, int batchSize) throws IOException;

    /**
     * @param writer 文件写入器
     * @param beanClass 数据类
     * @param paraMap 额外查询参数
     * @param <T> 数据类泛型
     * @throws IOException 抛出 IO 异常
     */
    <T> void export(FileWriter writer, Class<T> beanClass, Map<String, Object> paraMap) throws IOException;

    /**
     * @param writer 文件写入器
     * @param beanClass 数据类
     * @param paraMap 额外查询参数
     * @param batchSize 每次查询的数据条数
     * @param <T> 数据类泛型
     * @throws IOException 抛出 IO 异常
     */
    <T> void export(FileWriter writer, Class<T> beanClass, Map<String, Object> paraMap, int batchSize) throws IOException;

}
