package com.example.controller;

import com.ejlchina.searcher.BeanSearcher;
import com.ejlchina.searcher.MapSearcher;
import com.ejlchina.searcher.SearchResult;
import com.ejlchina.searcher.util.MapUtils;
import com.example.sbean.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private BeanSearcher beanSearcher;

	@Autowired
	private MapSearcher mapSearcher;

	/**
	 * 员工列表检索接口
	 */
	@GetMapping("/index")
	public Object index(HttpServletRequest request) {
		// 组合检索、排序、分页 和 统计 都在这一句代码中实现了
		return beanSearcher.search(Employee.class,				// 指定实体类
				MapUtils.flat(request.getParameterMap()), 	// 直接收集前端传来的检索参数，此种方式代码最为简洁
				new String[] { "age" });					// 统计字段：年龄
	}

	/**
	 * 员工列表检索接口
	 */
	@GetMapping("/map")
	public Object map(HttpServletRequest request) {
		// 组合检索、排序、分页 和 统计 都在这一句代码中实现了
		return mapSearcher.search(Employee.class,				// 指定实体类
				MapUtils.flat(request.getParameterMap()), 	// 直接收集前端传来的检索参数，此种方式代码最为简洁
				new String[] { "age" });					// 统计字段：年龄
	}

	// 以上代码等效于：

	@GetMapping("/index1")
	public SearchResult<Employee> index1(String name, String department, Integer page, Integer size, String sort, String order,
										@RequestParam(value = "name-op", required = false) String name_op,
										@RequestParam(value = "name-ic", required = false) boolean name_ic,
										@RequestParam(value = "age-0", required = false) Integer age_0,
										@RequestParam(value = "age-1", required = false) Integer age_1,
										@RequestParam(value = "age-op", required = false) String age_op,
										@RequestParam(value = "department-op", required = false) String department_op,
										@RequestParam(value = "department-ic", required = false) boolean department_ic,
										@RequestParam(value = "entryDate-0", required = false) String entryDate_0,
										@RequestParam(value = "entryDate-1", required = false) String entryDate_1,
										@RequestParam(value = "entryDate-op", required = false) String entryDate_op) {
		// 使用 MapUtils 构建检索参数
		Map<String, Object> params = MapUtils.builder()
				.field(Employee::getName, name).op(name_op).ic(name_ic)
				.field(Employee::getAge, age_0, age_1).op(age_op)
				.field(Employee::getDepartment, department).op(department_op).ic(department_ic)
//				.field(Employee::getEntryDate, entryDate_0, entryDate_1).op(entryDate_op)
				.orderBy(sort, order)
				.page(page, size)
				.build();
		// 组合检索、排序、分页 和 统计 都在这一句代码中实现了
		return beanSearcher.search(Employee.class, params, new String[] { "age" });
	}

	// 还等效于：

	@GetMapping("/index2")
	public SearchResult<Employee> index2(String name, String department, Integer page, Integer size, String sort, String order,
										@RequestParam(value = "name-op", required = false) String name_op,
										@RequestParam(value = "name-ic", required = false) String name_ic,
										@RequestParam(value = "age-0", required = false) Integer age_0,
										@RequestParam(value = "age-1", required = false) Integer age_1,
										@RequestParam(value = "age-op", required = false) String age_op,
										@RequestParam(value = "department-op", required = false) String department_op,
										@RequestParam(value = "department-ic", required = false) String department_ic,
										@RequestParam(value = "entryDate-0", required = false) String entryDate_0,
										@RequestParam(value = "entryDate-1", required = false) String entryDate_1,
										@RequestParam(value = "entryDate-op", required = false) String entryDate_op) {
		// 手动在 Map 里添加参数，此方式代码略微繁琐
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);					// 姓名检索值
		params.put("name-op", name_op);				// 姓名检索运算符
		params.put("name-ic", name_ic);				// 姓名是否忽略大小写
		params.put("age-0", age_0);					// 年龄第一个检索值
		params.put("age-1", age_1);					// 年龄第二个检索值
		params.put("age-op", age_op);				// 年龄检索运算符
		params.put("department", department);		// 部门检索值
		params.put("department-op", department_op);	// 部门检索运算符
		params.put("department-ic", department_ic);	// 部门是否忽略大小写
		params.put("entryDate-0", entryDate_0);		// 入职时间第一个检索值
		params.put("entryDate-1", entryDate_1);		// 入职时间第二个检索值
		params.put("entryDate-op", entryDate_op);	// 入职时间检索运算符
		params.put("page", page);					// 分页页数
		params.put("size", size);					// 分页大小
		params.put("sort", sort);					// 排序字段
		params.put("order", order);					// 排序方法：asc|desc
		// 组合检索、排序、分页 和 统计 都在这一句代码中实现了
		return beanSearcher.search(Employee.class, params, new String[] { "age" });
	}

}








