package com.example.controller;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.SearchResult;
import com.example.sbean.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private BeanSearcher beanSearcher;

//	/**
//	 * 员工列表检索接口
//	 */
//	@GetMapping("/index")
//	public SearchResult<Employee> index(@RequestParam Map<String, Object> params) {
//		// 组合检索、排序、分页 和 统计 都在这一句代码中实现了
//		return beanSearcher.search(Employee.class, params, Employee::getAge);
//	}

	/**
	 * 进阶版，Map 参数可以省略了，因为参数都在 AutoLoadParamFilter 里统一加载了
	 */
	@GetMapping("/index")
	public SearchResult<Employee> index() {
		// 组合检索、排序、分页 和 统计 都在这一句代码中实现了
		return beanSearcher.search(Employee.class, Employee::getAge);
	}


}








