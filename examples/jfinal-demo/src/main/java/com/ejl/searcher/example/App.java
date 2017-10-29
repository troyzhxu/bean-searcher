package com.ejl.searcher.example;

import com.ejl.searcher.Searcher;
import com.ejl.searcher.example.controller.UserController;
import com.ejl.searcher.support.SearchPlugin;
import com.ejl.searcher.support.SearchPlugin.SearcherReceiver;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;

public class App extends JFinalConfig implements SearcherReceiver{

	
	@Override
	public void configConstant(Constants me) {}

	
	@Override
	public void configRoute(Routes me) {
		me.add("/users", UserController.class);
	}
	

	@Override
	public void configEngine(Engine me) {}

	
	@Override
	public void configPlugin(Plugins me) {
		
		loadPropertyFile("dbconfig.properties");
		
		DruidPlugin druidPlugin = new DruidPlugin(getProperty("db.url"), 
				getProperty("db.username"), 
				getProperty("db.password"),
				getProperty("db.dirverClass"));
		druidPlugin.setMaxActive(getPropertyToInt("db.maxActive", 50));
		druidPlugin.setMinIdle(getPropertyToInt("db.minIdle", 10));

		SearchPlugin searchPlugin = new SearchPlugin(druidPlugin, "com.ejl.searcher.example.bean");
		
		searchPlugin.setShowSql(true);
		searchPlugin.setSearcherReceiver(this);
		
		me.add(druidPlugin);
		
		me.add(searchPlugin);
	}

	
	@Override
	public void receive(Searcher searcher) {
		Ioc.put(Searcher.class, searcher);
	}
	
	
	@Override
	public void configInterceptor(Interceptors me) {}

	
	@Override
	public void configHandler(Handlers me) {}




	
}
