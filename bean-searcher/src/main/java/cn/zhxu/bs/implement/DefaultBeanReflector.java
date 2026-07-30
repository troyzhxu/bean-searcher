package cn.zhxu.bs.implement;

import cn.zhxu.bs.*;
import cn.zhxu.bs.FieldConvertor.BFieldConvertor;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 默认查询结果解析器
 * 
 * @author Troy.Zhou @ 2017-03-20
 */
public class DefaultBeanReflector implements BeanReflector {

    private List<BFieldConvertor> convertors;

    public DefaultBeanReflector() {
        this(new ArrayList<>());
    }
    
    public DefaultBeanReflector(List<BFieldConvertor> convertors) {
        this.convertors = convertors;
    }
    
    @Override
    public <T> T reflect(BeanMeta<T> beanMeta, Collection<FieldMeta> fetchFields, Function<String, Object> valueGetter) {
        if (beanMeta.isRecord()) {
            return reflectRecord(beanMeta, fetchFields, valueGetter);
        }
        Class<T> beanClass = beanMeta.getBeanClass();
        T bean = newInstance(beanClass);
        for (FieldMeta meta : fetchFields) {
            Object value = valueGetter.apply(meta.getDbAlias());
            try {
                value = convert(meta, value);
            } catch (Exception e) {
                throw new SearchException(
                        "The type of [" + beanClass + "#" + meta.getName() + "] is mismatched with its database table field type", e);
            }
            if (value != null) {
                try {
                    meta.getField().set(bean, value);
                } catch (ReflectiveOperationException e) {
                    throw new SearchException(
                            "An exception occurred when setting value to [" + beanClass.getName() + "#" + meta.getName() + "], please check whether it's setter is correct.", e);
                }
            }
        }
        return bean;
    }

    /**
     * 反射构造 record 实例。
     * record 字段全部为 final，无法先 new 再 set，因此先将各字段值收集到按 canonical constructor 参数顺序排列的数组，
     * 再一次性调用 canonical constructor 完成实例化。
     */
    protected <T> T reflectRecord(BeanMeta<T> beanMeta, Collection<FieldMeta> fetchFields, Function<String, Object> valueGetter) {
        Class<T> beanClass = beanMeta.getBeanClass();
        RecordComponent[] components = beanClass.getRecordComponents();
        int componentCount = components.length;
        // 按 canonical constructor 参数类型顺序准备参数数组
        Class<?>[] paramTypes = new Class<?>[componentCount];
        Object[] args = new Object[componentCount];
        for (int i = 0; i < componentCount; i++) {
            paramTypes[i] = components[i].getType();
        }
        // 填充从数据库查询到的字段值
        for (FieldMeta meta : fetchFields) {
            int index = meta.getRecordIndex();
            if (index < 0 || index >= componentCount) {
                continue;
            }
            Object value = valueGetter.apply(meta.getDbAlias());
            try {
                value = convert(meta, value);
            } catch (Exception e) {
                throw new SearchException(
                        "The type of [" + beanClass + "#" + meta.getName() + "] is mismatch with it's database table field type", e);
            }
            args[index] = value;
        }
        // 调用 canonical constructor
        try {
            Constructor<T> constructor = beanClass.getDeclaredConstructor(paramTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (NoSuchMethodException e) {
            throw new SearchException("Can not find the canonical constructor of record [" + beanClass.getName() + "].", e);
        } catch (Exception e) {
            throw new SearchException("Can not instantiate record [" + beanClass.getName() + "] via its canonical constructor.", e);
        }
    }

    protected Object convert(FieldMeta meta, Object value) {
        if (value == null) {
            return null;
        }
        Class<?> valueType = value.getClass();
        Class<?> targetType = meta.getType();
        if (targetType.isAssignableFrom(valueType)) {
            // 如果 targetType 是 valueType 的父类，则直接返回
            return value;
        }
        for (FieldConvertor convertor: convertors) {
            if (convertor.supports(meta, valueType)) {
                return convertor.convert(meta, value);
            }
        }
        throw new SearchException("Can not convert " + valueType + " to " + targetType + " for " + meta.getBeanMeta().getBeanClass() +
                "#" + meta.getName() + ", please check the field type, or you can add a BFieldConvertor for it.");
    }

    protected <T> T newInstance(Class<T> beanClass) {
        try {
            return beanClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new SearchException("Can not instantiate [" + beanClass.getName() +
                    "], please check whether there is a constructor without parameters on it.", e);
        } catch (Exception e) {
            throw new SearchException("Can not instantiate [" + beanClass.getName() +
                    "], please check whether the constructor without parameters can be invoked without errors.", e);
        }
    }

    public List<BFieldConvertor> getConvertors() {
        return convertors;
    }

    public void setConvertors(List<BFieldConvertor> convertors) {
        this.convertors = Objects.requireNonNull(convertors);
    }

    public void addConvertor(BFieldConvertor convertor) {
        if (convertor != null) {
            convertors.add(convertor);
        }
    }

}

