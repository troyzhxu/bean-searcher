package com.ejlchina.searcher.support;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.ejlchina.searcher.SearcherBuilder;
import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.SearcherStarter;
import com.ejlchina.searcher.implement.MainSearcher;

/**
 * 自动启动的Spring Searcher
 * 
 * @author Troy.Zhou
 *
 */
public class SpringSearcher extends MainSearcher implements InitializingBean, DisposableBean {

	private String scanJar;
	private String scanPackage;

	private SearcherStarter searcherStarter;
	
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
			throw new SearcherException("SearchPlugin： scanPackage 不能为 空！");
		}
		if (searcherStarter == null) {
			searcherStarter = new SearcherStarter();
		}
		if (scanJar != null) {
			searcherStarter.start(scanJar, scanPackage);
		} else {
			searcherStarter.start(scanPackage);
		}
	}

	@Override
	public void destroy() throws Exception {
		searcherStarter.shutdown();
	}

	public void setScanJar(String scanJar) {
		this.scanJar = scanJar;
	}

	public void setScanPackage(String scanPackage) {
		this.scanPackage = scanPackage;
	}

	public void setSearcherStarter(SearcherStarter searcherStarter) {
		this.searcherStarter = searcherStarter;
	}
	
}
