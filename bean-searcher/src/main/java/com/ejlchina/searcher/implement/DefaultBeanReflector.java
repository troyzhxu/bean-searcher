package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.FieldConvertor.BFieldConvertor;

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
		Class<T> beanClass = beanMeta.getBeanClass();
		T bean = newInstance(beanClass);
		for (FieldMeta meta : fetchFields) {
			Object value = valueGetter.apply(meta.getDbAlias());
			try {
				value = convert(meta, value);
			} catch (Exception e) {
				throw new SearchException(
						"The type of [" + beanClass + "#" + meta.getName() + "] is mismatch with it's database table field type", e);
			}
			if (value != null) {
				try {
					meta.getField().set(bean, value);
				} catch (ReflectiveOperationException e) {
					throw new SearchException(
							"A exception occurred when setting value to [" + beanClass.getName() + "#" + meta.getName() + "], please check whether it's setter is correct.", e);
				}
			}
		}
		return bean;
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
