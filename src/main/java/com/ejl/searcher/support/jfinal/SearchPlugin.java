package com.ejl.searcher.support.jfinal;

import com.ejl.searcher.Searcher;
import com.ejl.searcher.SearcherBuilder;
import com.ejl.searcher.SearcherStarter;
import com.ejl.searcher.util.Ioc;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;

/***
 * 自动检索器插件，用于启动与生出检索器实例
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 */
public class SearchPlugin implements IPlugin {

	private String baseDir;
	private String scanJar;
	private String scanPackage;

	private SearcherStarter starter = SearcherStarter.starter();
	
	/**
	 * @param scanPackage 存放bean的package
	 */
	public SearchPlugin(String scanPackage) {
		this.scanPackage = scanPackage;
		this.baseDir = PathKit.getWebRootPath() + "/WEB-INF/classes/";
	}

	/**
	 * @param scanJar bean所在的jar名称
	 * @param scanPackage 存放bean的package
	 */
	public SearchPlugin(String scanJar, String scanPackage) {
		this.scanJar = scanJar;
		this.scanPackage = scanPackage;
		this.baseDir = PathKit.getWebRootPath() + "/WEB-INF/lib/";
	}

	@Override
	public boolean start() {
		if (scanPackage == null) {
			throw new RuntimeException("SearchPlugin： scanPackage 不能为 空！");
		}
		Ioc.add(Searcher.class, SearcherBuilder.builder()
				.configSearchSqlExecutor(new DbSearchSqlExecutor())
				.build());
		if (scanJar != null) {
			return starter.start(baseDir, scanJar, scanPackage);
		} else {
			return starter.start(baseDir, scanPackage);
		}
	}

	@Override
	public boolean stop() {
		starter.shutdown();
		return true;
	}

}
