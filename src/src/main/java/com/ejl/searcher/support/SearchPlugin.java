package com.ejl.searcher.support;

import javax.sql.DataSource;

import com.ejl.searcher.Searcher;
import com.ejl.searcher.SearcherBuilder;
import com.ejl.searcher.SearcherStarter;
import com.ejl.searcher.implement.MainSearchSqlExecutor;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;

/***
 * 自动检索器插件，用于启动与生成检索器实例
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 */
public class SearchPlugin implements IPlugin {

	private String scanJar;
	private String scanPackage;
	
	private IDataSourceProvider dataSourceProvider;

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
		Ioc.add(Searcher.class, SearcherBuilder.builder()
				.configSearchSqlExecutor(new MainSearchSqlExecutor(dataSource))
				.build());
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

}
