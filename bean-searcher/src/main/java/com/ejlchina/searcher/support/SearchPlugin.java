package com.ejlchina.searcher.support;

import javax.sql.DataSource;

import com.ejlchina.searcher.Searcher;
import com.ejlchina.searcher.SearcherBuilder;
import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.SearcherStarter;
import com.ejlchina.searcher.implement.MainSearchSqlExecutor;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;

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
	public static interface SearcherReceiver {
		
		void receive(Searcher searcher);
		
	}
	
	/**
	 * Searcher 配置器
	 * 
	 * @since 1.2.0
	 *
	 */
	public static interface SearcherConfiger {
		
		void config(SearcherBuilder builder);
		
	}
	
	
	private String[] scanPackages;
	
	private boolean showSql;
	
	private IDataSourceProvider dataSourceProvider;

	private SearcherReceiver searcherReceiver;
	
	private SearcherConfiger searcherConfiger;
	
	private SearcherStarter searcherStarter;
	

	/**
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
		MainSearchSqlExecutor searchSqlExecutor = new MainSearchSqlExecutor(dataSource);
		searchSqlExecutor.setShowSql(showSql);
		SearcherBuilder builder = SearcherBuilder.builder();
		builder.configSearchSqlExecutor(searchSqlExecutor);
		if (searcherConfiger != null) {
			searcherConfiger.config(builder);
		}
		if (searcherReceiver == null) {
			throw new SearcherException("You must config a SearcherReceiver for SearchPlugin!");
		}
		searcherReceiver.receive(builder.build());
		if (searcherStarter == null) {
			searcherStarter = new SearcherStarter();
		}
		return searcherStarter.start(scanPackages);
	}

	@Override
	public boolean stop() {
		searcherStarter.shutdown();
		return true;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	public void setSearcherReceiver(SearcherReceiver searcherReceiver) {
		this.searcherReceiver = searcherReceiver;
	}

	public void setSearcherConfiger(SearcherConfiger searcherConfiger) {
		this.searcherConfiger = searcherConfiger;
	}

	public void setSearcherStarter(SearcherStarter searcherStarter) {
		this.searcherStarter = searcherStarter;
	}
	
}
