package com.ejlchina.searcher;

import java.util.HashMap;
import java.util.Map;

import com.ejlchina.searcher.bean.User;
import com.ejlchina.searcher.param.Operator;

public class Application {

	
	
	public static void main(String[] args) {
		
		SearcherStarter starter = new SearcherStarter();
		starter.start("com.ejlchina.searcher.bean");
		
		Searcher searcher = SearcherBuilder.builder()
				.configSearchSqlExecutor(new SearchSqlExecutor() {
			
			@Override
			public SearchTmpResult execute(SearchSql searchSql) {
				System.out.println();
				System.out.println("LIST 	SQL 	::: " +  searchSql.getListSqlString());
				System.out.println("LIST 	PARAMS	::: " +  searchSql.getListSqlParams());
				System.out.println();
				System.out.println("CLUSTER	SQL 	::: " +  searchSql.getClusterSqlString());
				System.out.println("CLUSTER	PARAMS	::: " +  searchSql.getClusterSqlParams());
				System.out.println();
				return new SearchTmpResult(0);
			}
		}).build();
		
		Map<String, Object> params = new HashMap<>();
		params.put("name", "张");
		params.put("name-op", Operator.StartWith);
		
		params.put("age", 13);
		params.put("age-op", Operator.LessEqual);
		
		params.put("dateCreated-0", "2019-07-02");
		params.put("dateCreated-1", "2019-07-02 23:59");
		params.put("dateCreated-op", Operator.MultiValue);
		
		SearchResult<User> result = searcher.search(User.class, params);
		
		System.out.println("SEARCH RESULT	::: " + result);
		
		starter.shutdown();
	}
	

}
