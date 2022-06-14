package com.ejlchina.searcher.implement;

import java.time.temporal.Temporal;
import java.util.Date;

/**
 * 日期/时间格式化字段转换器
 * 该转换器可将数据库取出的 Date 对象字段 转换为 格式化的日期字符串
 * 与 {@link DefaultMapSearcher } 配合使用
 *
 * v3.0.0 支持 {@link Date } 及其子类的 日期格式化
 * v3.0.1 支持 {@link Temporal } 及其子类的 日期格式化
 *
 * @author Troy.Zhou @ 2021-11-03
 * @since v3.0.0
 *
 * this class will be removed in v4.0, please use {@link com.ejlchina.searcher.convertor.DateFormatFieldConvertor} instead.
 */
@Deprecated
public class DateFormatFieldConvertor extends com.ejlchina.searcher.convertor.DateFormatFieldConvertor {

}
