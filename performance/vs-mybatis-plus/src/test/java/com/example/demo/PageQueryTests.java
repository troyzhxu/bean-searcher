package com.example.demo;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.SearchResult;
import cn.zhxu.bs.util.MapUtils;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.Employee;
import com.example.demo.mapper.EmployeeMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * List + 总条数 分页查询测试
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PageQueryTests {

	@Autowired
	private BeanSearcher beanSearcher;

	@Autowired
	private EmployeeMapper employeeMapper;

	@Test
	@Order(1)
	void warmup() {
		System.out.println("热身...");
		System.out.println("Bean Searcher 与 MyBatis Plus 各执行 1000 次...");
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			beanSearcher.searchAll(Employee.class, MapUtils.builder()
					.field(Employee::getAge, 30)
					.build());
			employeeMapper.selectList(new LambdaQueryWrapper<>(Employee.class).eq(Employee::getAge, 30));
		}
		long cost = System.currentTimeMillis() - t0;
		System.out.println("热身完毕，耗时：" + cost + "ms");
	}

	@Test
	@Order(3)
	void testMyBatisPlus() {
		System.out.println("MyBatisPlus 执行 10000 次 ...");
		long t0 = System.currentTimeMillis();
		int selects = 0;
		long total = 0;
		for (int i = 0; i < 10000; i++) {
			Page<Employee> page = new Page<>(0, 15);
			Wrapper<Employee> wrapper = new LambdaQueryWrapper<>(Employee.class).eq(Employee::getAge, 30);
			Page<Employee> result = employeeMapper.selectPage(page, wrapper);
			selects += (int) result.getSize();
			total = result.getTotal();
		}
		long cost = System.currentTimeMillis() - t0;
		System.out.println("累计 Select 数量：" + selects);
		System.out.println("total：" + total);
		System.out.println("MyBatisPlus 耗时：" + cost + "ms");
	}

	@Test
	@Order(4)
	void testBeanSearcher() {
		System.out.println("BeanSearcher 执行 10000 次...");
		long t0 = System.currentTimeMillis();
		int selects = 0;
		long total = 0;
		for (int i = 0; i < 10000; i++) {
			SearchResult<Employee> result = beanSearcher.search(Employee.class, MapUtils.builder()
					.field(Employee::getAge, 30)
					.page(0, 15)
					.build());
			selects += result.getDataList().size();
			total = result.getTotalCount().longValue();
		}
		long cost = System.currentTimeMillis() - t0;
		System.out.println("累计 Select 数量：" + selects);
		System.out.println("total：" + total);
		System.out.println("BeanSearcher 耗时：" + cost + "ms");
	}

}
