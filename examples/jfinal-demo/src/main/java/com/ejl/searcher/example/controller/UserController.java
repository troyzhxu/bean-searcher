package com.ejl.searcher.example.controller;

import java.util.Map;

import com.ejl.searcher.SearchResult;
import com.ejl.searcher.Searcher;
import com.ejl.searcher.example.bean.StudentCourseBean;
import com.ejl.searcher.example.bean.UserBean;
import com.ejl.searcher.support.Ioc;
import com.ejl.searcher.util.MapUtils;
import com.jfinal.core.Controller;



public class UserController extends Controller {

	Searcher searcher = Ioc.get(Searcher.class);
	
	public void index() {
		
		Map<String, String[]> paraMap = getParaMap();
		Map<String, String> flatMap = MapUtils.flat(paraMap);
		
		SearchResult<UserBean> result = searcher.search(UserBean.class, flatMap);
		
		System.out.println("总条数：" + result.getTotalCount());
		System.out.println("本页条数：" + result.getDataList().size());
		
		renderJson(result);
		
	}
	
	public void courses() {
		
		Map<String, String> flatMap = MapUtils.flat(getParaMap());
		
		SearchResult<StudentCourseBean> result = searcher.search(StudentCourseBean.class, flatMap);
		
		renderJson(result);
	}
	
	
}
