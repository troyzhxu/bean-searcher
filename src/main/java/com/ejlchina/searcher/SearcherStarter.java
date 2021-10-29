package com.ejlchina.searcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;
import com.ejlchina.searcher.beanmap.SearchBeanMap;
import com.ejlchina.searcher.beanmap.SearchBeanCache;
import com.ejlchina.searcher.util.ClassScanner;
import com.ejlchina.searcher.util.StringUtils;

/***
 * 检索启动器 只有再启动之后才能进行检索 一般在应用程序启动时启动检索器
 * 
 * @author Troy.Zhou @ 2017-03-20        
 * 
 */
public class SearcherStarter {

	private final Logger log = LoggerFactory.getLogger(SearcherStarter.class);

	/**
	 * @param basePackages 可检索 Bean 所在的 package，可检索 Bean 是被 @SearchBean 注解的 Bean
	 * @return the count of bean scanned
	 */
	public int start(String... basePackages) {
		log.info("Bean Searcher Starting...");
		ClassScanner classScanner = new ClassScanner();
		Set<Class<?>> classes = classScanner.scan(basePackages);
		int count = startWithBeanClassList(classes);
		if (count < 1) {
			log.warn("Bean Searcher had scanned " + count + " beans");
		} else {
			log.info("Bean Searcher had scanned " + count + " beans");
		}
		log.info("Bean Searcher Start completed with packages: " + Arrays.toString(basePackages));
		return count;
	}


	/**
	 * 关闭搜索器，释放资源
	 */
	public void shutdown() {
		SearchBeanCache.clear();
		log.info("Bean Searcher shutdown!");
	}
	
	
	protected int startWithBeanClassList(Set<Class<?>> beanClassSet) {
		int beanCount = 0;
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
			SearchBeanCache.addSearchBeanMap(beanClass, searchBeanMap);
			beanCount++;
		}
		return beanCount;
	}

	
}
