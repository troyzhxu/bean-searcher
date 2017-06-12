package com.ejl.searcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.ejl.searcher.annotation.DbField;
import com.ejl.searcher.annotation.SearchBean;
import com.ejl.searcher.beanmap.SearchBeanMap;
import com.ejl.searcher.beanmap.SearchBeanMapCache;
import com.ejl.searcher.utils.StrUtils;
import com.ejl.searcher.utils.clazz.ClassScanner;

/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 *         检索启动器 只有再启动之后才能进行检索 一般在应用程序启动时启动检索器
 * 
 */
public class SearcherStarter {

	private static SearcherStarter starter = new SearcherStarter();

	/**
	 * 获取一个检索启动器
	 */
	public static SearcherStarter starter() {
		return starter;
	}

	/**
	 * @param packageName
	 *            可检索 Bean 所在的 package，可检索 Bean 是被 @SearchBean 注解的 Bean
	 * @return true if start successfully, else return false
	 */
	public boolean start(String baseDir, String packageName) {
		List<Class<?>> classList = ClassScanner.scan(baseDir, packageName);
		return startWithBeanClassList(classList);
	}

	/**
	 * @param baseDir
	 *            存放 jar 的路径
	 * @param jarName
	 *            可检索 Bean 所在的 jar 名称
	 * @param packageName
	 *            可检索 Bean 所在的 package，可检索 Bean 是被 @SearchBean 注解的 Bean
	 * @return true if start successfully, else return false
	 */
	public boolean start(String baseDir, String jarName, String packageName) {
		List<Class<?>> classList = ClassScanner.scan(baseDir, jarName, packageName);
		return startWithBeanClassList(classList);
	}

	/**
	 * 关闭搜索器，释放资源
	 */
	public void shutdown() {
		SearchBeanMapCache.sharedCache().clear();
	}

	private boolean startWithBeanClassList(List<Class<?>> beanClassList) {
		SearchBeanMapCache searchBeanMapCache = SearchBeanMapCache.sharedCache();
		for (Class<?> beanClass : beanClassList) {
			SearchBean searchBean = beanClass.getAnnotation(SearchBean.class);
			if (searchBean != null) {
				SearchBeanMap searchBeanMap = new SearchBeanMap(searchBean.tables(), searchBean.joinCond(),
						searchBean.groupBy(), searchBean.distinct());
				for (Field field : beanClass.getDeclaredFields()) {
					DbField dbField = field.getAnnotation(DbField.class);
					if (dbField != null) {
						Method method = null;
						String fieldName = field.getName();
						Class<?> fieldType = field.getType();
						try {
							method = beanClass.getMethod("set" + StrUtils.firstCharToUpperCase(fieldName), fieldType);
						} catch (Exception e) {
							throw new RuntimeException(
									"【" + beanClass.getName() + "：" + fieldName + "】被注解的属性必须要有正确的set方法！");
						}
						searchBeanMap.addFieldDbMap(fieldName, dbField.value().trim(), method, fieldType);
					}
				}
				if (searchBeanMap.getFieldList().size() == 0) {
					throw new RuntimeException("【" + beanClass.getName() + "】" + "】没有被@DbFile注解的属性！");
				}
				searchBeanMapCache.addSearchBeanMap(beanClass, searchBeanMap);
			}
		}
		return true;
	}

}
