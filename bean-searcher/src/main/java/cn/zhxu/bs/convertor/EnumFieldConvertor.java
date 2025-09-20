package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.SearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [字符串 | 整型 to 枚举] 字段转换器（v3.7.0 起支持枚举序号转换）
 * @author Troy.Zhou @ 2021-11-12
 * @since v3.2.0（v3.8.0 之前在 com.ejlchina.searcher.implement 包下）
 */
public class EnumFieldConvertor implements FieldConvertor.BFieldConvertor {

    static final Logger log = LoggerFactory.getLogger(EnumFieldConvertor.class);

    /**
     * @since v3.7.0
     * 当数据库值不能转换为对应的枚举时，是否抛出异常
     */
    private boolean failOnError = true;

    /**
     * @since v3.7.0
     * 当数据库值为字符串，匹配枚举时是否忽略大小写
     */
    private boolean ignoreCase = false;

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        if (valueType == String.class || valueType == int.class || valueType == Integer.class
                || valueType == short.class || valueType == Short.class
                || valueType == byte.class || valueType == Byte.class) {
            return Enum.class.isAssignableFrom(meta.getType());
        }
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        if (value instanceof String) {
            return doConvert(meta.getType(), (String) value);
        }
        if (value instanceof Integer) {
            return doConvert(meta.getType(), (Integer) value);
        }
        if (value instanceof Short) {
            return doConvert(meta.getType(), (Short) value);
        }
        if (value instanceof Byte) {
            return doConvert(meta.getType(), ((Byte) value));
        }
        throw new IllegalStateException("The supports(FieldMeta, Class<?>) method must be called first and return true before convert(FieldMeta, Object) method can be called");
    }

    @SuppressWarnings("all")
    protected Object doConvert(Class<?> targetType, String name) {
        if (ignoreCase) {
            for (Object v : targetType.getEnumConstants()) {
                if (((Enum<?>) v).name().equalsIgnoreCase(name)) {
                    return v;
                }
            }
            if (failOnError) {
                throw new SearchException("Can not convert [" + name + "] to " + targetType);
            }
        } else {
            try {
                return Enum.valueOf((Class<? extends Enum>) targetType, name);
            } catch (IllegalArgumentException e) {
                if (failOnError) throw e;
            }
        }
        log.warn("Can not convert [{}] to {}", name, targetType);
        return null;
    }

    protected Object doConvert(Class<?> targetType, int ordinal) {
        for (Object v : targetType.getEnumConstants()) {
            if (((Enum<?>) v).ordinal() == ordinal) {
                return v;
            }
        }
        if (failOnError) {
            throw new IllegalArgumentException("Can not convert [" + ordinal + "] to " + targetType);
        }
        log.warn("Can not convert [{}] to {}", ordinal, targetType);
        return null;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

}
