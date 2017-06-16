package com.ejl.searcher.example.spring.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ejl.searcher.SearchResult;
import com.ejl.searcher.Searcher;
import com.ejl.searcher.example.spring.bean.UserBean;
import com.ejl.searcher.util.MapUtils;


@Controller
@RequestMapping("/users")
public class UserController {

	
	@Autowired
	private Searcher searcher;
	

	@RequestMapping
	public @ResponseBody SearchResult<UserBean> index(HttpServletRequest request) {		
		
		return searcher.search(UserBean.class, MapUtils.flat(request.getParameterMap()));
		
	}
	
	
}
