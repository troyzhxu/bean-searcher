package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.IllegalParamException;
import cn.zhxu.bs.bean.DbType;
import cn.zhxu.bs.util.StringUtils;

import java.math.BigDecimal;

/**
 * [String | Number to Number] 参数值转换器
 *
 * @author Troy.Zhou @ 2022-06-14
 * @since v3.8.0
 */
public class NumberParamConvertor implements FieldConvertor.ParamConvertor {

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        DbType dbType = meta.getDbType();
        return (
                dbType == DbType.BYTE || dbType == DbType.SHORT || dbType == DbType.INT || dbType == DbType.LONG ||
                        dbType == DbType.FLOAT || dbType == DbType.DOUBLE || dbType == DbType.DECIMAL
        ) && (
                String.class == valueType || Byte.class == valueType || Short.class == valueType ||
                        Integer.class == valueType || Long.class == valueType || Float.class == valueType ||
                        Double.class == valueType || BigDecimal.class == valueType
        ) && !Enum.class.isAssignableFrom(meta.getType());
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        DbType dbType = meta.getDbType();
        try {
            return doConvert(dbType, value);
        } catch (NumberFormatException e) {
            throw new IllegalParamException("Field type is " + dbType + ", but the param value is: " + value, e);
        }
    }

    protected Object doConvert(DbType dbType, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            String s = (String) value;
            if (StringUtils.isBlank(s)) {
                return null;
            }
            switch (dbType) {
                case BYTE:
                    return Byte.parseByte(s);
                case SHORT:
                    return Short.parseShort(s);
                case INT:
                    return Integer.parseInt(s);
                case LONG:
                    return Long.parseLong(s);
                case FLOAT:
                    return Float.parseFloat(s);
                case DOUBLE:
                    return Double.parseDouble(s);
                case DECIMAL:
                    return new BigDecimal(s);
            }
        }
        if (value instanceof Number) {
            Number num = (Number) value;
            switch (dbType) {
                case BYTE:
                    return num.byteValue();
                case SHORT:
                    return num.shortValue();
                case INT:
                    return num.intValue();
                case LONG:
                    return num.longValue();
                case FLOAT:
                    return num.floatValue();
                case DOUBLE:
                    return num.doubleValue();
            }
            // 转为 BigDecimal
            if (num instanceof BigDecimal) {
                return num;
            }
            if (num instanceof Byte || num instanceof Short || num instanceof Integer || num instanceof Long) {
                return new BigDecimal(num.longValue());
            }
            if (num instanceof Float || num instanceof Double) {
                return BigDecimal.valueOf(num.doubleValue());
            }
        }
        throw new NumberFormatException(value.toString());
    }

}
