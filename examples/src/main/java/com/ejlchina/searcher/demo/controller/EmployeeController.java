package com.ejlchina.searcher.demo.controller;

import javax.servlet.http.HttpServletRequest;

import com.ejlchina.searcher.util.MapUtils;
import com.ejlchina.searcher.demo.bean.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejlchina.searcher.Searcher;

import java.util.Map;


@RestController
@RequestMapping("/employee")
public class EmployeeController {

	
	private final Searcher searcher;

	@Autowired
	public EmployeeController(Searcher searcher) {
		this.searcher = searcher;
	}


	@RequestMapping("/index")
	public Object index(HttpServletRequest request) {
		Map<String, Object> params = MapUtils.flat(request.getParameterMap());
		return searcher.search(Employee.class, params);
	}
	
	
}
