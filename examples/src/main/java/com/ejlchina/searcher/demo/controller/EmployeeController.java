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
		// 使用 bean-searcher 提供的工具栏MapUtils 收集页面请求参数
		Map<String, Object> params = MapUtils.flat(request.getParameterMap());
		// 调用 bean-searcher 提供的 searcher 接口检索数据并返回
		return searcher.search(Employee.class, params);
	}
	
	
}
