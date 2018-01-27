package com.ejl.searcher.demo.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ejl.searcher.SearchResult;
import com.ejl.searcher.Searcher;
import com.ejl.searcher.demo.bean.UserBean;
import com.ejl.searcher.util.MapUtils;


@Controller
@RequestMapping("/users")
public class UserController {

	
	@Autowired
	private Searcher searcher;
	


	@RequestMapping("/index")
	public SearchResult<UserBean> index(HttpServletRequest request) {		
		
		// 取得请求参数Map集合
		Map<String, String> flatMap = MapUtils.flat(request.getParameterMap());
		
		// 把参数传入检索器，检索出结果
		SearchResult<UserBean> result = searcher.search(UserBean.class, flatMap);
		
		return result;
	}
	
	
}
