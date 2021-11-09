package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * [字符串 to 数字] 字段转换器
 * @author Troy.Zhou @ 2021-11-01
 * @since v3.0.0
 */
public class StrNumFieldConvertor implements FieldConvertor.BFieldConvertor {

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType, Class<?> targetType) {
        return valueType == String.class && (
               targetType == int.class || targetType == Integer.class ||
               targetType == long.class || targetType == Long.class ||
               targetType == float.class || targetType == Float.class ||
               targetType == double.class || targetType == Double.class ||
               targetType == short.class || targetType == Short.class ||
               targetType == byte.class || targetType == Byte.class ||
               targetType == BigDecimal.class || targetType == BigInteger.class
        );
    }

    @Override
    public Object convert(FieldMeta meta, Object value, Class<?> targetType) {
        String number = (String) value;
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(number);
        }
        if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(number);
        }
        if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(number);
        }
        if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(number);
        }
        if (targetType == short.class || targetType == Short.class) {
            return Short.parseShort(number);
        }
        if (targetType == byte.class || targetType == Byte.class) {
            return Byte.parseByte(number);
        }
        if (targetType == BigDecimal.class) {
            return new BigDecimal(number);
        }
        if (targetType == BigInteger.class) {
            return new BigInteger(number);
        }
        throw new UnsupportedOperationException();
    }

}
