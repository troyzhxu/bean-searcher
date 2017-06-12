package com.ejl.searcher;

import java.util.HashMap;

import com.ejl.searcher.SearchTmpData;


public class MainSearcherTest {

	
	public static void main(String[] args) {
		/*
		SearcherStarter.starter().start("com.ejl.bean");
		
		Searcher searcher = SearcherBuilder.builder().configSearchSqlExecutor(new SearchSqlExecutor() {
			
			@Override
			public SearchTmpResult execute(SearchSql searchSql) {
				System.out.println("查询列表SQL:");
				System.out.println(searchSql.getListSqlString());
				System.out.println("参数：" + Arrays.toString(searchSql.getListSqlParams().toArray()));
				System.out.println();
				System.out.println("查询总数SQL:");
				System.out.println(searchSql.getCountSqlString());
				System.out.println("参数：" + Arrays.toString(searchSql.getCountSqlParams().toArray()));
				System.out.println();
				SearchTmpResult result = new SearchTmpResult();
				result.setTotalCount(2);
				TmpData data = new TmpData();
				data.put("u_id", 1);
				data.put("u_name", "jack");
				data.put("s_name", "苏州大学");
				result.addTmpData(data);
				data = new TmpData();
				data.put("u_id", 2);
				data.put("u_name", "tom");
				data.put("s_name", "上海大学");
				result.addTmpData(data);
				return result;
			}
		}).build();
		
		
		Map<String, String> paraMap = getRequestMap();
		
	//	SearchResult<UserBean> result = searcher.search(UserBean.class, paraMap);
		*/ 

	}
	
/*
	private static Map<String, String> getRequestMap() {
		Map<String, String> paraMap = new HashMap<>();
		// 过滤参数
		paraMap.put("name", "jack");
		paraMap.put("name_ic", "on");
		paraMap.put("name_op", "sw");
		paraMap.put("sex", "16");
		paraMap.put("sex_op", "gt");
		// 排序参数
		paraMap.put("sort", "name");
		paraMap.put("order", "asc");
		// 分页参数
		paraMap.put("max", "20");
		paraMap.put("offset", "16");
		// 无用参数
		paraMap.put("max1", "20");
		paraMap.put("name2", "16");
		paraMap.put("name2_op", "sw");
		return paraMap;
	}
	*/
	
}


class TmpData extends HashMap<String, Object> implements SearchTmpData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object get(String feild) {
		return get((Object)feild);
	}


	
}
