package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.SearchResult;
import com.ejlchina.searcher.SearchResultConvertInfo;
import com.ejlchina.searcher.SearchResultResolver;
import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.bean.BeanAware;
import com.ejlchina.searcher.implement.convertor.FieldConvertor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 默认查询结果解析器
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 */
public class MainSearchResultResolver implements SearchResultResolver {

	
	private FieldConvertor fieldConvertor;

	
	public MainSearchResultResolver() {
	}
	
	
	public MainSearchResultResolver(FieldConvertor fieldConvertor) {
		this.fieldConvertor = fieldConvertor;
	}

	
	@Override
	public <T> SearchResult<T> resolve(SearchResultConvertInfo<T> convertInfo, SearchResult<Map<String, Object>> searchMapResult) {
		Class<T> beanClass = convertInfo.getBeanClass();
		Set<Entry<String, String>> fieldDbAliasEntrySet = convertInfo.getFieldDbAliasEntrySet();
		Map<String, Method> fieldGetMethodMap = convertInfo.getFieldGetMethodMap();
		Map<String, Class<?>> fieldTypeMap = convertInfo.getFieldTypeMap();
		SearchResult<T> searchResult = new SearchResult<T>(searchMapResult.getTotalCount(),
				searchMapResult.getSummaries());
		List<Map<String, Object>> tmpDataList = searchMapResult.getDataList();
		
		for (Map<String, Object> tmpData : tmpDataList) {
			T bean;
			try {
				bean = beanClass.newInstance();
			} catch (Exception e) {
				throw new SearcherException("为【" + beanClass.getName() + "】创建对象时报错，请检查该类中是否有无参构造方法！", e);
			}
			for (Entry<String, String> entry : fieldDbAliasEntrySet) {
				String field = entry.getKey();
				String dbAlias = entry.getValue();
				Object value = tmpData.get(dbAlias);
				Class<?> fieldType = fieldTypeMap.get(field);
				Method method = fieldGetMethodMap.get(field);
				try {
					value = fieldConvertor.convert(value, fieldType);
				} catch (Exception e) {
					throw new SearcherException(
							"可检索Bean【" + beanClass + "】的属性【" + field + "】的类型【" + fieldType + "】与数据库字段类型不兼容！", e);
				}
				try {
					method.invoke(bean, value);
				} catch (Exception e) {
					throw new SearcherException(
							"为【" + beanClass.getName() + "】的【" + field + "】属性赋值时报错，请检查该属性的set方法参数类型是否正确！", e);
				}
			}
			if (bean instanceof BeanAware) {
				((BeanAware) bean).afterAssembly();
			}
			searchResult.addData(bean);
		}
		return searchResult;
	}

	public void setFieldConvertor(FieldConvertor fieldConvertor) {
		this.fieldConvertor = fieldConvertor;
	}
	
	
}
