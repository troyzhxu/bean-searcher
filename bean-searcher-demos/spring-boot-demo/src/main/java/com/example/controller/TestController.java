package com.example.controller;

import com.ejlchina.searcher.MapSearcher;
import com.ejlchina.searcher.util.MapUtils;
import com.example.sbean.DFieldBean;
import com.example.sbean.DTableBean;
import com.example.sbean.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/test")
public class TestController {

	@Autowired
	private MapSearcher mapSearcher;

	@GetMapping("/index")
	public Object index() {
		// 组合检索、排序、分页 和 统计 都在这一句代码中实现了
		return mapSearcher.search(Employee.class,
				MapUtils.builder()
						.onlySelect(Employee::getId)
						.onlySelect(Employee::getAge)
						.field(Employee::getName, "j").op("in").ic()
						.build()
		);
	}




	@GetMapping("/dynamic-field")
	public Object dynamicField() {
		Map<String, Object> map = new HashMap<>();
		// 动态指定查询的字段
		map.put("fieldName", "name");
		return mapSearcher.searchList(DFieldBean.class, map);
	}

	@GetMapping("/dynamic-table")
	public Object dynamicTable() {
		Map<String, Object> map = new HashMap<>();
		// 动态指定查询的表名
		map.put("table", "department");
		return mapSearcher.searchList(DTableBean.class, map);
	}

}
