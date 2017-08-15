package com.ejl.searcher;

import com.ejl.searcher.dialect.MySqlDialect;
import com.ejl.searcher.implement.MainSearchParamResolver;
import com.ejl.searcher.implement.MainSearchResultResolver;
import com.ejl.searcher.implement.MainSearchSqlResolver;
import com.ejl.searcher.implement.MainSearcher;
import com.ejl.searcher.implement.convert.DefaultFieldConvertor;

/***
 * 检索器 Builder
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 * */
public class SearcherBuilder {

	private SearchParamResolver searchParamResolver;
	
	private SearchSqlResolver searchSqlResolver;
	
	private SearchSqlExecutor searchSqlExecutor;
	
	private SearchResultResolver searchResultResolver;
	
	private int prifexSeparatorLength = 1;
	
	public static SearcherBuilder builder() {
		return new SearcherBuilder();
	}
	
	public SearcherBuilder configSearchParamResolver(SearchParamResolver searchParamResolver) {
		this.searchParamResolver = searchParamResolver;
		return this;
	}
	
	public SearcherBuilder configSearchSqlResolver(SearchSqlResolver searchSqlResolver) {
		this.searchSqlResolver = searchSqlResolver;
		return this;
	}
	
	public SearcherBuilder configSearchSqlExecutor(SearchSqlExecutor searchSqlExecutor) {
		this.searchSqlExecutor = searchSqlExecutor;
		return this;
	}
	
	public SearcherBuilder configSearchResultResolver(SearchResultResolver searchResultResolver) {
		this.searchResultResolver = searchResultResolver;
		return this;
	}
	
	public SearcherBuilder configPrifexSeparatorLength(int length) {
		this.prifexSeparatorLength = length;
		return this;
	}
	
	
	public Searcher build() {
		return build(new MainSearcher());
	}
	
	public Searcher build(MainSearcher mainSearcher) {
		if (searchParamResolver != null) {
			mainSearcher.setSearchParamResolver(searchParamResolver);
		} else {
			mainSearcher.setSearchParamResolver(new MainSearchParamResolver());
		}
		if (searchSqlResolver != null) {
			mainSearcher.setSearchSqlResolver(searchSqlResolver);
		} else {
			MainSearchSqlResolver searchSqlResolver = new MainSearchSqlResolver();
			searchSqlResolver.setDialect(new MySqlDialect());
			mainSearcher.setSearchSqlResolver(searchSqlResolver);
		}
		if (searchSqlExecutor != null) {
			mainSearcher.setSearchSqlExecutor(searchSqlExecutor);
		} else {
			throw new SearcherBuildFailureException("你必须配置一个 searchSqlExecutor，才能建立一个检索器！ ");
		}
		if (searchResultResolver != null) {
			mainSearcher.setSearchResultResolver(searchResultResolver);
		} else {
			MainSearchResultResolver searchResultResolver = new MainSearchResultResolver();
			searchResultResolver.setFieldConvertor(new DefaultFieldConvertor());
			mainSearcher.setSearchResultResolver(searchResultResolver);
		}
		mainSearcher.setPrifexSeparatorLength(prifexSeparatorLength);
		return mainSearcher;
	}
	
	
	
}
