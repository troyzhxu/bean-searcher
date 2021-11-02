package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * 默认查询结果解析器
 * 
 * @author Troy.Zhou @ 2017-03-20
 */
public class DefaultBeanReflector implements BeanReflector {

	private List<FieldConvertor> convertors = new ArrayList<>();

	public DefaultBeanReflector() {
	}
	
	public DefaultBeanReflector(List<FieldConvertor> convertors) {
		this.convertors = convertors;
	}
	
	@Override
	public <T> T reflect(Metadata<T> metadata, Function<String, Object> valueGetter) {
		Class<T> beanClass = metadata.getBeanClass();
		Collection<FieldMeta> fieldMetas = metadata.getFieldMetas();
		T bean = newInstance(beanClass);
		for (FieldMeta meta : fieldMetas) {
			String field = meta.getName();
			String dbAlias = meta.getDbAlias();
			Class<?> fieldType = meta.getType();
			Object value = valueGetter.apply(dbAlias);
			try {
				value = convert(value, fieldType);
			} catch (Exception e) {
				throw new SearchException(
						"The type of [" + beanClass + "#" + field + "] is mismatch with it's database table field type", e);
			}
			if (value != null) {
				try {
					meta.getSetter().invoke(bean, value);
				} catch (ReflectiveOperationException e) {
					throw new SearchException(
							"A exception occurred when setting value to [" + beanClass.getName() + "#" + field + "], please check whether it's setter is correct.", e);
				}
			}
		}
		return bean;
	}

	protected Object convert(Object value, Class<?> targetType) {
		if (value == null) {
			return null;
		}
		Class<?> valueType = value.getClass();
		if (targetType.isAssignableFrom(valueType)) {
			// 如果 targetType 是 valueType 的父类，则直接返回
			return value;
		}
		Exception ex = null;
		for (FieldConvertor convertor: convertors) {
			if (convertor.supports(valueType, targetType)) {
				try {
					return convertor.convert(value, targetType);
				} catch (Exception e) {
					if (ex != null) {
						e.initCause(ex);
					}
					ex = e;
				}
			}
		}
		throw new SearchException("不能把【" + valueType + "】类型的数据库值转换为【" + targetType + "】类型的字段值，你可以添加一个 FieldConvertor 来转换它！", ex);
	}

	protected <T> T newInstance(Class<T> beanClass) {
		try {
			return beanClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new SearchException("为【" + beanClass.getName() + "】创建对象时报错，请检查该类中是否有无参构造方法！", e);
		}
	}

	public List<FieldConvertor> getConvertors() {
		return convertors;
	}

	public void setConvertors(List<FieldConvertor> convertors) {
		this.convertors = Objects.requireNonNull(convertors);
	}

	public void addConvertor(FieldConvertor convertor) {
		if (convertor != null) {
			convertors.add(convertor);
		}
	}

}
