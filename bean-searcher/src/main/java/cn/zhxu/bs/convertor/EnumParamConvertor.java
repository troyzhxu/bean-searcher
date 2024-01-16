package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.IllegalParamException;
import cn.zhxu.bs.bean.DbType;

/**
 * [String | Enum to Number（枚举序号） | String（枚举名）] 参数值转换器
 *
 * @author Troy.Zhou @ 2023-07-13
 * @since v4.2.1
 */
public class EnumParamConvertor implements FieldConvertor.ParamConvertor {

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        Class<?> targetType = meta.getType();
        if (targetType != null && Enum.class.isAssignableFrom(targetType)) {
            DbType dbType = meta.getDbType();
            return dbType == DbType.INT && (valueType == String.class || valueType == targetType) ||
                    dbType == DbType.STRING && valueType == targetType;
        }
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        Class<?> targetType = meta.getType();
        if (value instanceof Enum<?>) {
            DbType dbType = meta.getDbType();
            if (dbType == DbType.INT) {
                return ((Enum<?>) value).ordinal();
            }
            if (dbType == DbType.STRING) {
                return ((Enum<?>) value).name();
            }
            throw new IllegalStateException("Invalid dbType: " + dbType);
        }
        if (value instanceof String) {
            String enumValue = value.toString();
            for (Object v : targetType.getEnumConstants()) {
                Enum<?> e = (Enum<?>) v;
                if (e.name().equalsIgnoreCase(enumValue)) {
                    return e.ordinal();
                }
            }
            try {
                return Integer.parseInt(enumValue);
            } catch (Exception e) {
                throw new IllegalParamException(meta.getName() + ": " + enumValue);
            }
        }
        throw new IllegalStateException("only string or enum value accepted");
    }

}
