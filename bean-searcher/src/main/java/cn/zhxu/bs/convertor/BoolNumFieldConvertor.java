package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;

/**
 * [Boolean to Integer | Long | Short | Byte] 字段转换器
 *
 * @author Troy.Zhou @ 2021-11-03
 * @since v3.6.1（v3.8.0 之前在 com.ejlchina.searcher.implement 包下）
 */
public class BoolNumFieldConvertor implements FieldConvertor.BFieldConvertor {

    private static final Integer I0 = 0;
    private static final Integer I1 = 1;
    private static final Long L0 = 0L;
    private static final Long L1 = 1L;
    private static final Short S0 = 0;
    private static final Short S1 = 1;
    private static final Byte B0 = 0;
    private static final Byte B1 = 1;

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        if (Boolean.class == valueType) {
            Class<?> targetType = meta.getType();
            return (
                    targetType == int.class || targetType == Integer.class ||
                    targetType == long.class || targetType == Long.class ||
                    targetType == short.class || targetType == Short.class ||
                    targetType == byte.class || targetType == Byte.class
            );
        }
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        Class<?> targetType = meta.getType();
        if (targetType == int.class || targetType == Integer.class) {
            return (Boolean) value ? I1 : I0;
        }
        if (targetType == long.class || targetType == Long.class) {
            return (Boolean) value ? L1 : L0;
        }
        if (targetType == short.class || targetType == Short.class) {
            return (Boolean) value ? S1 : S0;
        }
        if (targetType == byte.class || targetType == Byte.class) {
            return (Boolean) value ? B1 : B0;
        }
        throw new IllegalStateException("Unsupported targetType: " + targetType);
    }

}
