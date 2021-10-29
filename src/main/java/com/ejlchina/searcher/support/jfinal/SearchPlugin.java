package com.ejlchina.searcher.support.jfinal;

import com.ejlchina.searcher.Searcher;
import com.ejlchina.searcher.SearcherBuilder;
import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.implement.MainSearchSqlExecutor;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;

import javax.sql.DataSource;

/***
 * 自动检索器Jfinal插件，用于启动与生出检索器实例
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 */
public class SearchPlugin implements IPlugin {
	
	/**
	 * Searcher 接收器
	 * 
	 * @since 1.2.0
	 *
	 */
	public interface SearcherReceiver {
		
		void receive(Searcher searcher);
		
	}
	
	/**
	 * Searcher 配置器
	 * 
	 * @since 1.2.0
	 *
	 */
	public interface SearcherConfiger {
		
		void config(SearcherBuilder builder);
		
	}
	
	private final String[] scanPackages;
	
	private final IDataSourceProvider dataSourceProvider;

	private SearcherReceiver searcherReceiver;
	
	private SearcherConfiger searcherConfiger;


	/**
	 * @param dataSourceProvider dataSourceProvider
	 * @param scanPackages 存放bean的package
	 */
	public SearchPlugin(IDataSourceProvider dataSourceProvider, String... scanPackages) {
		this.scanPackages = scanPackages;
		this.dataSourceProvider = dataSourceProvider;
	}

	
	@Override
	public boolean start() {
		if (scanPackages == null || scanPackages.length == 0) {
			throw new SearcherException("SearchPlugin： scanPackages 不能为 空！");
		}
		DataSource dataSource = dataSourceProvider.getDataSource();
		if (dataSource == null) {
			throw new SearcherException("Can not get DataSource from IDataSourceProvider, "
					+ "please confirm IDataSourceProvider is in front of SearchPlugin");
		}
		SearcherBuilder builder = SearcherBuilder.builder();
		builder.configSearchSqlExecutor(new MainSearchSqlExecutor(dataSource));
		if (searcherConfiger != null) {
			searcherConfiger.config(builder);
		}
		if (searcherReceiver == null) {
			throw new SearcherException("You must config a SearcherReceiver for SearchPlugin!");
		}
		searcherReceiver.receive(builder.build());
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}

	public void setSearcherReceiver(SearcherReceiver searcherReceiver) {
		this.searcherReceiver = searcherReceiver;
	}

	public void setSearcherConfiger(SearcherConfiger searcherConfiger) {
		this.searcherConfiger = searcherConfiger;
	}

}
