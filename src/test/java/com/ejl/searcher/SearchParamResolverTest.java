package com.ejl.searcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zhxu.searcher.implement.MainSearchParamResolver;
import com.zhxu.searcher.param.SearchParam;

public class SearchParamResolverTest {

	
	
	
	public static void main(String[] args) {
		Map<String, String> paraMap = new HashMap<>();
		
		
		List<String> fieldList = new ArrayList<>();
		fieldList.add("name");
		fieldList.add("sex");
		
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

		MainSearchParamResolver resolver = new MainSearchParamResolver();

		
		SearchParam searchParam = resolver.resolve(fieldList, paraMap);
		

		System.out.println(searchParam);
		

		/**
		 * 期望输出：
		 * sort = name
		 * order = asc
		 * max = 20
		 * offset = 16
		 * FilterParam[ name = name, value = jack, ignoreCase = true,  operator = sw ]
		 * FilterParam[ name = sex,  value = 16,   ignoreCase = false, operator = gt ]
		 * 
		 * */
		
	}
	
	
}
