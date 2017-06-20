package com.ejl.searcher.support;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.ejl.searcher.SearcherBuilder;
import com.ejl.searcher.SearcherStarter;
import com.ejl.searcher.implement.MainSearcher;

/**
 * 自动启动的Spring Searcher
 * 
 * @author Troy.Zhou
 *
 */
public class SpringSearcher extends MainSearcher implements InitializingBean, DisposableBean {

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
		if (scanJar != null) {
			SearcherStarter.starter().start(scanJar, scanPackage);
		} else {
			SearcherStarter.starter().start(scanPackage);
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
