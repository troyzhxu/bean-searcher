package com.ejlchina.searcher;

import com.ejlchina.searcher.dialect.MySqlDialect;
import com.ejlchina.searcher.implement.*;
import com.ejlchina.searcher.implement.convertor.DefaultFieldConvertor;
import com.ejlchina.searcher.implement.processor.DefaultParamProcessor;
import com.ejlchina.searcher.virtual.DefaultVirtualParamProcessor;
import com.ejlchina.searcher.virtual.VirtualParamProcessor;

/***
 * 检索器 Builder
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 * */
public class SearcherBuilder {

	/**
	 * 用于构建一个 BeanSearcher 实例
	 */
	public static BeanSearcherBuilder beanSearcher() {
		return new BeanSearcherBuilder();
	}

	/**
	 * 用于构建一个 MapSearcher 实例
	 */
	public static MapSearcherBuilder mapSearcher() {
		return new MapSearcherBuilder();
	}

	@SuppressWarnings("unchecked")
	static class DefaultSearcherBuilder<Builder extends DefaultSearcherBuilder<?>> {

		private SearchParamResolver searchParamResolver;

		private SearchSqlResolver searchSqlResolver;

		private SearchSqlExecutor searchSqlExecutor;

		private VirtualParamProcessor virtualParamProcessor;

		public Builder searchParamResolver(SearchParamResolver searchParamResolver) {
			this.searchParamResolver = searchParamResolver;
			return (Builder) this;
		}

		public Builder searchSqlResolver(SearchSqlResolver searchSqlResolver) {
			this.searchSqlResolver = searchSqlResolver;
			return (Builder) this;
		}

		public Builder searchSqlExecutor(SearchSqlExecutor searchSqlExecutor) {
			this.searchSqlExecutor = searchSqlExecutor;
			return (Builder) this;
		}

		public Builder virtualParamProcessor(VirtualParamProcessor virtualParamProcessor) {
			this.virtualParamProcessor = virtualParamProcessor;
			return (Builder) this;
		}

		protected void buildInternal(DefaultSearcher mainSearcher) {
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
				searchSqlResolver.setParamProcessor(new DefaultParamProcessor());
				mainSearcher.setSearchSqlResolver(searchSqlResolver);
			}
			if (searchSqlExecutor != null) {
				mainSearcher.setSearchSqlExecutor(searchSqlExecutor);
			} else {
				throw new SearcherException("你必须配置一个 searchSqlExecutor，才能建立一个检索器！ ");
			}
			if (virtualParamProcessor != null) {
				mainSearcher.setVirtualParamProcessor(virtualParamProcessor);
			} else {
				mainSearcher.setVirtualParamProcessor(new DefaultVirtualParamProcessor());
			}
		}

	}


	public static class BeanSearcherBuilder extends DefaultSearcherBuilder<BeanSearcherBuilder> {

		private SearchResultResolver searchResultResolver;

		public BeanSearcherBuilder searchResultResolver(SearchResultResolver searchResultResolver) {
			this.searchResultResolver = searchResultResolver;
			return this;
		}

		public BeanSearcher build() {
			return build(new DefaultBeanSearcher());
		}

		public BeanSearcher build(DefaultBeanSearcher beanSearcher) {
			buildInternal(beanSearcher);
			if (searchResultResolver != null) {
				beanSearcher.setSearchResultResolver(searchResultResolver);
			} else {
				MainSearchResultResolver searchResultResolver = new MainSearchResultResolver();
				searchResultResolver.setFieldConvertor(new DefaultFieldConvertor());
				beanSearcher.setSearchResultResolver(searchResultResolver);
			}
			return beanSearcher;
		}

	}

	public static class MapSearcherBuilder extends DefaultSearcherBuilder<MapSearcherBuilder> {

		public MapSearcher build() {
			return build(new DefaultMapSearcher());
		}

		public MapSearcher build(DefaultMapSearcher beanSearcher) {
			buildInternal(beanSearcher);
			return beanSearcher;
		}

	}
	
}
