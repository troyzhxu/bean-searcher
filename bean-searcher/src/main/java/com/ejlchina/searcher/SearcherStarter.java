package com.ejlchina.searcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;
import com.ejlchina.searcher.beanmap.SearchBeanMap;
import com.ejlchina.searcher.beanmap.SearchBeanMapCache;
import com.ejlchina.searcher.util.ClassScanner;
import com.ejlchina.searcher.util.StringUtils;

/***
 * 检索启动器 只有再启动之后才能进行检索 一般在应用程序启动时启动检索器
 * 
 * @author Troy.Zhou @ 2017-03-20        
 * 
 */
public class SearcherStarter {

	

	/**
	 * @param basePackages 可检索 Bean 所在的 package，可检索 Bean 是被 @SearchBean 注解的 Bean
	 * @return true if start successfully, else return false
	 */
	public boolean start(String... basePackages) {
		ClassScanner classScanner = new ClassScanner();
		Set<Class<?>> classes = classScanner.scan(basePackages);
		return startWithBeanClassList(classes);
	}


	/**
	 * 关闭搜索器，释放资源
	 */
	public void shutdown() {
		SearchBeanMapCache.sharedCache().clear();
	}
	
	
	protected boolean startWithBeanClassList(Set<Class<?>> beanClassSet) {
		SearchBeanMapCache searchBeanMapCache = SearchBeanMapCache.sharedCache();
		for (Class<?> beanClass : beanClassSet) {
			SearchBean searchBean = beanClass.getAnnotation(SearchBean.class);
			if (searchBean == null) {
				continue;
			}
			SearchBeanMap searchBeanMap = new SearchBeanMap(searchBean.tables(), searchBean.joinCond(),
					searchBean.groupBy(), searchBean.distinct());
			for (Field field : beanClass.getDeclaredFields()) {
				DbField dbField = field.getAnnotation(DbField.class);
				if (dbField == null) {
					continue;
				}
				Method method = null;
				String fieldName = field.getName();
				Class<?> fieldType = field.getType();
				try {
					method = beanClass.getMethod("set" + StringUtils.firstCharToUpperCase(fieldName), fieldType);
				} catch (Exception e) {
					throw new SearcherException(
							"【" + beanClass.getName() + "：" + fieldName + "】被注解的属性必须要有正确的set方法！", e);
				}
				searchBeanMap.addFieldDbMap(fieldName, dbField.value().trim(), method, fieldType);
			}
			if (searchBeanMap.getFieldList().size() == 0) {
				throw new SearcherException("【" + beanClass.getName() + "】" + "】没有被@DbFile注解的属性！");
			}
			searchBeanMapCache.addSearchBeanMap(beanClass, searchBeanMap);
		}
		return true;
	}

	
}
