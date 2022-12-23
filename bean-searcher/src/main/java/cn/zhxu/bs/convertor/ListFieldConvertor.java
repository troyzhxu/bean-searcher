package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.implement.DefaultBeanReflector;
import cn.zhxu.bs.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * [字符串 to List] 字段转换器
 * 与 {@link DefaultBeanReflector } 配合使用
 * @author Troy.Zhou @ 2021-11-01
 * @since v4.0.0
 */
public class ListFieldConvertor implements FieldConvertor.BFieldConvertor {

    private String itemSeparator = ",";
    private List<Convertor<?>> convertors = Collections.emptyList();

    public interface Convertor<T> {
        T convert(String value);
    }

    public ListFieldConvertor() { }

    public ListFieldConvertor(String itemSeparator) {
        this.itemSeparator = Objects.requireNonNull(itemSeparator);
    }

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        if (valueType == String.class) {
            return meta.getType() == List.class;
        }
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        String strValue = (String) value;
        if (StringUtils.isBlank(strValue)) {
            return Collections.emptyList();
        }
        Stream<String> stream = Arrays.stream(strValue.split(itemSeparator));
        Type genericType = meta.getField().getGenericType();
        if (genericType instanceof ParameterizedType) {
            Type type = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            return stream.map((v) -> convert(type, v)).collect(Collectors.toList());
        }
        return stream.collect(Collectors.toList());
    }

    protected Object convert(Type type, String value) {
        if (type == Integer.class) {
            return Integer.parseInt(value);
        }
        if (type == Long.class) {
            return Long.parseLong(value);
        }
        if (type == Short.class) {
            return Short.parseShort(value);
        }
        if (type == Byte.class) {
            return Byte.parseByte(value);
        }
        if (type == Float.class) {
            return Float.parseFloat(value);
        }
        if (type == Double.class) {
            return Double.parseDouble(value);
        }
        if (type == BigDecimal.class) {
            return new BigDecimal(value);
        }
        if (type == BigInteger.class) {
            return new BigInteger(value);
        }
        for (Convertor<?> convertor : convertors) {
            if (match(type, convertor.getClass())) {
                return convertor.convert(value);
            }
        }
        return value;
    }

    protected boolean match(Type targetType, Class<?> convertorClass) {
        Type type = convertorClass.getGenericInterfaces()[0];
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments()[0] == targetType;
        }
        return false;
    }

    public String getItemSeparator() {
        return itemSeparator;
    }

    public void setItemSeparator(String itemSeparator) {
        this.itemSeparator = Objects.requireNonNull(itemSeparator);
    }

    public List<Convertor<?>> getConvertors() {
        return convertors;
    }

    public void setConvertors(List<Convertor<?>> convertors) {
        this.convertors = Objects.requireNonNull(convertors);
    }

}
