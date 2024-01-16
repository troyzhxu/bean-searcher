package com.example.demo;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.SearchResult;
import cn.zhxu.bs.util.MapUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

	private final BeanSearcher beanSearcher;

    public EmployeeController(BeanSearcher beanSearcher) {
        this.beanSearcher = beanSearcher;
    }

    /**
	 * 员工列表检索接口
	 */
	@GetMapping("/index")
	public Object index(@RequestParam Map<String, Object> params) {
		// 组合检索、排序、分页 和 统计 都在这一句代码中实现了
		return beanSearcher.search(Employee.class, params, Employee::getAge);
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
				.field(Employee::getEntryDate, entryDate_0, entryDate_1).op(entryDate_op)
				.orderBy(sort, order)
				.page(page != null ? page : 0, size != null ? size : 15)
				.build();
		// 组合检索、排序、分页 和 统计 都在这一句代码中实现了
		return beanSearcher.search(Employee.class, params, Employee::getAge);
	}

}








