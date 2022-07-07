package com.ejlchina.searcher;

/**
 * @author Troy.Zhou @ 2022-07-07
 *
 * 数据库字段值转换接口
 * 用于把 数据库查出的字段值 型转为 另外一种值
 *
 * 由于该接口缺少 {@link FieldConvertor} 引入的 {@link FieldConvertor#supports} 检测方法，
 * 为防止误判常通过 {@link com.ejlchina.searcher.bean.DbField#converter} 绑定使用
 *
 * @since v3.8.1
 */
public interface Convertor {

    /**
     * 把 value 转换为 targetType 类型的数据
     * v3.2.0 后移除冗余参数 targetType，该参数可通过 meta.getType() 获取
     * @param meta 需要转换的字段元信息（非空）
     * @param value 从数据库取出的待转换的值（非空）
     * @return 转换目标值
     * */
    Object convert(FieldMeta meta, Object value);

}
