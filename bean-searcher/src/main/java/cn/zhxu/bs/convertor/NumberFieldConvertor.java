package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.implement.DefaultBeanReflector;

/**
 * [数字 to 数字] 字段转换器
 * 与 {@link DefaultBeanReflector } 配合使用
 * @author Troy.Zhou @ 2021-11-01
 * @since v3.0.0（v3.8.0 之前在 com.ejlchina.searcher.implement 包下）
 */
public class NumberFieldConvertor implements FieldConvertor.BFieldConvertor {

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        if (Number.class.isAssignableFrom(valueType)) {
            Class<?> targetType = meta.getType();
            return (
                    targetType == int.class || targetType == Integer.class ||
                    targetType == long.class || targetType == Long.class ||
                    targetType == float.class || targetType == Float.class ||
                    targetType == double.class || targetType == Double.class ||
                    targetType == short.class || targetType == Short.class ||
                    targetType == byte.class || targetType == Byte.class
            );
        }
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        Number number = (Number) value;
        Class<?> targetType = meta.getType();
        if (targetType == int.class || targetType == Integer.class) {
            return number.intValue();
        }
        if (targetType == long.class || targetType == Long.class) {
            return number.longValue();
        }
        if (targetType == float.class || targetType == Float.class) {
            return number.floatValue();
        }
        if (targetType == double.class || targetType == Double.class) {
            return number.doubleValue();
        }
        if (targetType == short.class || targetType == Short.class) {
            return number.shortValue();
        }
        if (targetType == byte.class || targetType == Byte.class) {
            return number.byteValue();
        }
        throw new IllegalStateException("Unsupported targetType: " + targetType);
    }

}


