package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;

/**
 * [字符串 to 枚举] 字段转换器
 * @author Troy.Zhou @ 2021-11-12
 * @since v3.2.0
 */
public class EnumFieldConvertor implements FieldConvertor.BFieldConvertor {

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        if (valueType == String.class) {
            return Enum.class.isAssignableFrom(meta.getType());
        }
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        return convertToEnum(meta.getType(), (String) value);
    }

    @SuppressWarnings("unchecked")
    protected <T extends Enum<T>> T convertToEnum(Class<?> targetType, String name) {
        return Enum.valueOf((Class<T>) targetType, name);
    }

}
