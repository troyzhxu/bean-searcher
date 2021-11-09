package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.FieldConvertor.BFieldConvertor;

import java.util.ArrayList;
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
	public <T> T reflect(BeanMeta<T> beanMeta, List<String> fetchFields, Function<String, Object> valueGetter) {
		Class<T> beanClass = beanMeta.getBeanClass();
		T bean = newInstance(beanClass);
		for (String field : fetchFields) {
			FieldMeta meta = beanMeta.requireFieldMeta(field);
			Object value = valueGetter.apply(meta.getDbAlias());
			try {
				value = convert(meta, value);
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
			if (convertor.supports(meta, valueType, targetType)) {
				return convertor.convert(meta, value, targetType);
			}
		}
		throw new SearchException("不能把【" + valueType + "】类型的数据库值转换为【" + targetType + "】类型的字段值，你可以添加一个 FieldConvertor 来转换它！");
	}

	protected <T> T newInstance(Class<T> beanClass) {
		try {
			return beanClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new SearchException("为【" + beanClass.getName() + "】创建对象时报错，请检查该类中是否有无参构造方法！", e);
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
