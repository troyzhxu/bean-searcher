package com.ejlchina.searcher.support.spring;

import com.ejlchina.searcher.SearcherBuilder;
import com.ejlchina.searcher.implement.MainSearcher;
import org.springframework.beans.factory.InitializingBean;

/**
 * 请直接使用 MainSearcher
 */
public class SpringSearcher extends MainSearcher implements InitializingBean {

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
	}

	@Deprecated
	public void setScanPackages(String[] scanPackages) { }
	
}
