package com.ejl.searcher.support;

import javax.sql.DataSource;

import com.ejl.searcher.Searcher;
import com.ejl.searcher.SearcherBuilder;
import com.ejl.searcher.SearcherStarter;
import com.ejl.searcher.implement.MainSearchSqlExecutor;
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
	 * @since 1.1.3
	 *
	 */
	public static interface SearcherReceiver {
		
		void receive(Searcher searcher);
		
	}
	
	
	private String scanJar;
	private String scanPackage;
	
	private boolean showSql;
	
	private IDataSourceProvider dataSourceProvider;

	
	private SearcherReceiver searcherReceiver;
	
	
	private SearcherStarter starter = SearcherStarter.starter();
	
	/**
	 * @param scanPackage 存放bean的package
	 */
	public SearchPlugin(IDataSourceProvider dataSourceProvider, String scanPackage) {
		this(dataSourceProvider, null, scanPackage);
	}

	/**
	 * @param scanJar bean所在的jar名称
	 * @param scanPackage 存放bean的package
	 */
	public SearchPlugin(IDataSourceProvider dataSourceProvider, String scanJar, String scanPackage) {
		this.scanJar = scanJar;
		this.scanPackage = scanPackage;
		this.dataSourceProvider = dataSourceProvider;
	}

	@Override
	public boolean start() {
		if (scanPackage == null) {
			throw new RuntimeException("SearchPlugin： scanPackage 不能为 空！");
		}
		DataSource dataSource = dataSourceProvider.getDataSource();
		if (dataSource == null) {
			throw new RuntimeException("Can not get DataSource from IDataSourceProvider, "
					+ "please confirm IDataSourceProvider is in front of SearchPlugin");
		}
		MainSearchSqlExecutor searchSqlExecutor = new MainSearchSqlExecutor(dataSource);
		searchSqlExecutor.setShowSql(showSql);
		Searcher searcher = SearcherBuilder.builder()
				.configSearchSqlExecutor(searchSqlExecutor)
				.build();
		searcherReceiver.receive(searcher);
		if (scanJar != null) {
			return starter.start(scanJar, scanPackage);
		} else {
			return starter.start(scanPackage);
		}
	}

	@Override
	public boolean stop() {
		starter.shutdown();
		return true;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	public void setSearcherReceiver(SearcherReceiver searcherReceiver) {
		this.searcherReceiver = searcherReceiver;
	}
	
}
