package com.ejl.searcher.implement;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ejl.searcher.SearchResult;
import com.ejl.searcher.SearchResultConvertInfo;
import com.ejl.searcher.SearchResultResolver;
import com.ejl.searcher.bean.BeanAware;
import com.ejl.searcher.temp.SearchTmpData;
import com.ejl.searcher.temp.SearchTmpResult;
import com.ejl.searcher.util.FieldConvertor;

import java.util.Set;

/**
 * 默认查询结果解析器
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 */
public class MainSearchResultResolver implements SearchResultResolver {

	
	private FieldConvertor fieldConvertor;
	
	@Override
	public <T> SearchResult<T> resolve(SearchResultConvertInfo<T> convertInfo, SearchTmpResult searchTmpResult) {
		
		Class<T> beanClass = convertInfo.getBeanClass();
		Set<Entry<String, String>> fieldDbAliasEntrySet = convertInfo.getFieldDbAliasEntrySet();
		Map<String, Method> fieldGetMethodMap = convertInfo.getFieldGetMethodMap();
		Map<String, Class<?>> fieldTypeMap = convertInfo.getFieldTypeMap();
		SearchResult<T> searchResult = new SearchResult<T>(searchTmpResult.getTotalCount());
		List<SearchTmpData> tmpDataList = searchTmpResult.getTmpDataList();
		
		for (SearchTmpData tmpData : tmpDataList) {
			T bean;
			try {
				bean = beanClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("为【" + beanClass.getName() + "】创建对象时报错，请检查该类中是否有无参构造方法！", e);
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
					throw new RuntimeException(
							"可检索Bean【" + beanClass + "】的属性【" + field + "】的类型【" + fieldType + "】与数据库字段类型不兼容！", e);
				}
				try {
					method.invoke(bean, value);
				} catch (Exception e) {
					throw new IllegalArgumentException(
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
