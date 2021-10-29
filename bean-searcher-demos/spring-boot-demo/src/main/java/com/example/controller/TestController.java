package com.example.controller;

import com.ejlchina.searcher.Searcher;
import com.example.sbean.DFieldBean;
import com.example.sbean.DTableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/test")
public class TestController {

	@Autowired
	private Searcher searcher;

	@GetMapping("/dynamic-field")
	public Object dynamicField() {
		Map<String, Object> map = new HashMap<>();
		// 动态指定查询的字段
		map.put("fieldName", "name");
		return searcher.searchList(DFieldBean.class, map);
	}

	@GetMapping("/dynamic-table")
	public Object dynamicTable() {
		Map<String, Object> map = new HashMap<>();
		// 动态指定查询的表名
		map.put("table", "department");
		return searcher.searchList(DTableBean.class, map);
	}

}
