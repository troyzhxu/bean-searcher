package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.FieldConvertor.BFieldConvertor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 默认查询结果解析器
 * 
 * @author Troy.Zhou @ 2017-03-20
 */
public class DefaultBeanReflector implements BeanReflector {

	private List<BFieldConvertor> convertors;
	private Map<Class<?>, Convertor> onCallConvertors;

	public DefaultBeanReflector() {
		this(new ArrayList<>());
	}
	
	public DefaultBeanReflector(List<BFieldConvertor> convertors) {
		this.convertors = convertors;
		this.onCallConvertors = new ConcurrentHashMap<>();
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
		if (meta.getConvClazz() != null) {
			Convertor convertor = getOnCallConvertor(meta.getConvClazz());
			return convertor.convert(meta, value);
		}
		for (FieldConvertor convertor: convertors) {
			if (convertor.supports(meta, valueType)) {
				return convertor.convert(meta, value);
			}
		}
		throw new SearchException("不能把【" + valueType + "】类型的数据库值转换为【" + targetType + "】类型的字段值，你可以添加一个 BFieldConvertor 来转换它！");
	}

	protected <T> T newInstance(Class<T> beanClass) {
		try {
			return beanClass.getDeclaredConstructor().newInstance();
		} catch (NoSuchMethodException e) {
			throw new SearchException("Can not instantiate [" + beanClass.getName() +
					"], please check whether there is a constructor without parameters in the class.", e);
		} catch (Exception e) {
			throw new SearchException("Can not instantiate [" + beanClass.getName() +
					"], please check whether the constructor without parameters can be invoked without errors.", e);
		}
	}

	protected Convertor getOnCallConvertor(Class<? extends Convertor> convClazz) {
		if (!onCallConvertors.containsKey(convClazz)) {
			Convertor convertor = newInstance(convClazz);
			onCallConvertors.put(convClazz, convertor);
		}
		return onCallConvertors.get(convClazz);
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

	public Map<Class<?>, Convertor> getOnCallConvertors() {
		return onCallConvertors;
	}

	public void setOnCallConvertors(Map<Class<?>, Convertor> convertors) {
		this.onCallConvertors = Objects.requireNonNull(convertors);
	}

	public void addOnCallConvertor(BFieldConvertor convertor) {
		if (convertor != null) {
			onCallConvertors.put(convertor.getClass(), convertor);
		}
	}

}
