package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [字符串 | 整型 to 枚举] 字段转换器（v3.7.0 起支持枚举序号转换）
 * @author Troy.Zhou @ 2021-11-12
 * @since v3.2.0
 */
public class EnumFieldConvertor implements FieldConvertor.BFieldConvertor {

    static final Logger log = LoggerFactory.getLogger(EnumFieldConvertor.class);

    /**
     * @since v3.7.0
     * 当数据库值不能转换为对应的枚举时，是否抛出异常
     */
    private boolean failOnError = true;

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        if (valueType == String.class || valueType == int.class || valueType == Integer.class) {
            return Enum.class.isAssignableFrom(meta.getType());
        }
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        if (value instanceof String) {
            return convertToEnum(meta.getType(), (String) value);
        }
        if (value instanceof Integer) {
            return convertToEnum(meta.getType(), (Integer) value);
        }
        throw new IllegalStateException("The supports(FieldMeta, Class<?>) method must be called first and return true before convert(FieldMeta, Object) method can be called");
    }

    @SuppressWarnings("unchecked")
    protected <T extends Enum<T>> T convertToEnum(Class<?> targetType, String name) {
        if (failOnError) {
            return Enum.valueOf((Class<T>) targetType, name);
        }
        try {
            return Enum.valueOf((Class<T>) targetType, name);
        } catch (IllegalArgumentException e) {
            log.warn("can not convert [{}] to {}", name, targetType);
        }
        return null;
    }

    protected Object convertToEnum(Class<?> targetType, int ordinal) {
        for (Object v : targetType.getEnumConstants()) {
            if (((Enum<?>) v).ordinal() == ordinal) {
                return v;
            }
        }
        if (failOnError) {
            throw new IllegalArgumentException("can not convert [" + ordinal + "] to " + targetType);
        }
        log.warn("can not convert [{}] to {}", ordinal, targetType);
        return null;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

}
