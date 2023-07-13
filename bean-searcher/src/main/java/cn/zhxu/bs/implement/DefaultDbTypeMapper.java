package cn.zhxu.bs.implement;

import cn.zhxu.bs.DbMapping;
import cn.zhxu.bs.bean.DbType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * 默认数据库字段类型识别器
 * @since v3.8.0
 */
public class DefaultDbTypeMapper implements DbMapping.DbTypeMapper {

    private DbType enumAutoMapTo = DbType.INT;

    public DefaultDbTypeMapper() {
    }

    public DefaultDbTypeMapper(DbType enumAutoMapTo) {
        this.enumAutoMapTo = enumAutoMapTo;
    }

    @Override
    public DbType map(Class<?> fieldType) {
        if (fieldType == boolean.class || fieldType == Boolean.class) {
            return DbType.BOOL;
        }
        if (fieldType == byte.class || fieldType == Byte.class) {
            return DbType.BYTE;
        }
        if (fieldType == short.class || fieldType == Short.class) {
            return DbType.SHORT;
        }
        if (fieldType == int.class || fieldType == Integer.class) {
            return DbType.INT;
        }
        if (fieldType == long.class || fieldType == Long.class) {
            return DbType.LONG;
        }
        if (fieldType == float.class || fieldType == Float.class) {
            return DbType.FLOAT;
        }
        if (fieldType == double.class || fieldType == Double.class) {
            return DbType.DOUBLE;
        }
        if (fieldType == BigDecimal.class) {
            return DbType.DECIMAL;
        }
        if (fieldType == String.class) {
            return DbType.STRING;
        }
        if (fieldType == java.sql.Date.class || fieldType == LocalDate.class) {
            return DbType.DATE;
        }
        if (fieldType == java.sql.Time.class || fieldType == LocalTime.class) {
            return DbType.TIME;
        }
        if (fieldType == Date.class || fieldType == Timestamp.class || fieldType == LocalDateTime.class) {
            return DbType.DATETIME;
        }
        // 枚举，对应的 DbType 默认为 INT
        if (enumAutoMapTo != null && Enum.class.isAssignableFrom(fieldType)) {
            return enumAutoMapTo;
        }
        return DbType.UNKNOWN;
    }

    public DbType getEnumAutoMapTo() {
        return enumAutoMapTo;
    }

    public void setEnumAutoMapTo(DbType enumAutoMapTo) {
        this.enumAutoMapTo = enumAutoMapTo;
    }

}
