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


	private String[] scanPackages;

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
		if (scanPackages == null || scanPackages.length == 0) {
			throw new SearcherException("SpringSearcher： scanPackage 不能为 空！");
		}
		if (searcherStarter == null) {
			searcherStarter = new SearcherStarter();
		}
		searcherStarter.start(scanPackages);
	}

	@Override
	public void destroy() throws Exception {
		searcherStarter.shutdown();
	}
	

	public void setScanPackages(String[] scanPackages) {
		this.scanPackages = scanPackages;
	}

	public void setSearcherStarter(SearcherStarter searcherStarter) {
		this.searcherStarter = searcherStarter;
	}
	
}
