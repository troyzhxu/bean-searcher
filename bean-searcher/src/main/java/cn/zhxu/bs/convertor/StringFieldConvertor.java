package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.SearchException;

import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * [Clob | Number | Boolean | Date to String] 字段转换器
 * @author Troy.Zhou @ 2025-09-22
 * @since v4.6.0
 */
public class StringFieldConvertor implements FieldConvertor.BFieldConvertor {

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        if (meta.getType() == String.class) {
            return Clob.class.isAssignableFrom(valueType)
                    || valueType == LocalDate.class
                    || valueType == LocalDateTime.class
                    || Date.class.isAssignableFrom(valueType)
                    || Number.class.isAssignableFrom(valueType)
                    || valueType == Boolean.class;
        }
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        if (value instanceof Clob) {
            return fromClob((Clob) value);
        }
        if (value instanceof LocalDate) {
            return ((LocalDate) value).toString();
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).toString();
        }
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate().toString();
        }
        if (value instanceof java.sql.Time) {
            return ((java.sql.Time) value).toLocalTime().toString();
        }
        if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format((Date) value);
        }
        return value.toString();
    }

    public static String fromClob(Clob clob) {
        try {
            return clob.getSubString(1, (int) clob.length());
        } catch (SQLException e) {
            throw new SearchException("can not get string from Clob object: " + e.getMessage(), e);
        }
    }

}
