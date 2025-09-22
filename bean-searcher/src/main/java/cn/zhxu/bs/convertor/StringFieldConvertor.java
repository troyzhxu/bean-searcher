package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.SearchException;

import java.sql.Clob;
import java.sql.SQLException;

/**
 * [Clob | Number | Boolean to String] 字段转换器
 * @author Troy.Zhou @ 2025-09-22
 * @since v4.6.0
 */
public class StringFieldConvertor implements FieldConvertor.BFieldConvertor {

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        if (meta.getType() == String.class) {
            return valueType == Clob.class || valueType == Boolean.class || Number.class.isAssignableFrom(valueType);
        }
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        if (value instanceof Clob) {
            Clob clob = (Clob) value;
            try {
                return clob.getSubString(1, (int) clob.length());
            } catch (SQLException e) {
                throw new SearchException("can not get string from Clob object: " + e.getMessage(), e);
            }
        }
        return value.toString();
    }

}
