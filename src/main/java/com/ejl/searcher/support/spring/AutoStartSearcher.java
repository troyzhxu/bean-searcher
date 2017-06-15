package com.ejl.searcher.support.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.ejl.searcher.SearcherBuilder;
import com.ejl.searcher.SearcherStarter;
import com.ejl.searcher.implement.MainSearcher;

/**
 * 
 * @author Troy.Zhou
 *
 */
public class AutoStartSearcher extends MainSearcher implements InitializingBean, DisposableBean {

	private String scanJar;
	private String scanPackage;

	@Override
	public void afterPropertiesSet() throws Exception {
		SearcherBuilder.builder()
				.configSearchParamResolver(getSearchParamResolver())
				.configSearchResultResolver(getSearchResultResolver())
				.configSearchSqlExecutor(getSearchSqlExecutor())
				.configSearchSqlResolver(getSearchSqlResolver())
				.configPrifexSeparatorLength(getPrifexSeparatorLength())
				.build(this);
		if (scanPackage == null) {
			throw new RuntimeException("SearchPlugin： scanPackage 不能为 空！");
		}
		String baseDir = AutoStartSearcher.class.getClassLoader().getResource("").getPath();
		if (scanJar != null) {
			baseDir = baseDir.substring(0, baseDir.length() - 8) + "lib/";
			SearcherStarter.starter().start(baseDir, scanJar, scanPackage);
		} else {
			SearcherStarter.starter().start(baseDir, scanPackage);
		}
	}

	@Override
	public void destroy() throws Exception {
		SearcherStarter.starter().shutdown();
	}

	public void setScanJar(String scanJar) {
		this.scanJar = scanJar;
	}

	public void setScanPackage(String scanPackage) {
		this.scanPackage = scanPackage;
	}
	
}
