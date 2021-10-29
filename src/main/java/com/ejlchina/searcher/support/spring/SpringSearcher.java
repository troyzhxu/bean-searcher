package com.ejlchina.searcher.support.spring;

import com.ejlchina.searcher.SearcherBuilder;
import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.implement.MainSearcher;
import org.springframework.beans.factory.InitializingBean;

/**
 * 自动启动的Spring Searcher
 * 
 * @author Troy.Zhou
 *
 */
public class SpringSearcher extends MainSearcher implements InitializingBean {


	private String[] scanPackages;
	
	@Override
	public void afterPropertiesSet() {
		SearcherBuilder.builder()
				.configSearchParamResolver(getSearchParamResolver())
				.configSearchResultResolver(getSearchResultResolver())
				.configSearchSqlExecutor(getSearchSqlExecutor())
				.configSearchSqlResolver(getSearchSqlResolver())
				.configVirtualParamProcessor(getVirtualParamProcessor())
				.configPrifexSeparatorLength(getPrifexSeparatorLength())
				.build(this);
		if (scanPackages == null || scanPackages.length == 0) {
			throw new SearcherException("SpringSearcher： scanPackage 不能为 空！");
		}

	}

	public void setScanPackages(String[] scanPackages) {
		this.scanPackages = scanPackages;
	}
	
}
