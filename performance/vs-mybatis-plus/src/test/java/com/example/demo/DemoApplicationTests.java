package com.example.demo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ejlchina.searcher.BeanSearcher;
import com.ejlchina.searcher.MapSearcher;
import com.ejlchina.searcher.util.MapUtils;
import com.example.demo.entity.Employee;
import com.example.demo.mapper.EmployeeMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private BeanSearcher beanSearcher;

	@Autowired
	private MapSearcher mapSearcher;

	@Autowired
	private EmployeeMapper employeeMapper;

	@Test
	void warmup() {
		System.out.println("热身...");
		System.out.println("Bean Searcher 与 MyBatis Plus 各执行 100 次...");
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			employeeMapper.selectList(null);
		}
		for (int i = 0; i < 50; i++) {
			beanSearcher.searchAll(Employee.class, null);
			mapSearcher.searchAll(Employee.class, null);
		}
		long cost = System.currentTimeMillis() - t0;
		System.out.println("热身完毕，耗时：" + cost + "ms");
	}


	@Test
	void testMapSearcher() {
		System.out.println("MapSearcher 执行 10000 次...");
		long t0 = System.currentTimeMillis();
		int totalCount = 0;
		for (int i = 0; i < 10000; i++) {
			List<Map<String, Object>> list = mapSearcher.searchAll(Employee.class, MapUtils.builder()
					.field(Employee::getAge, 30)
					.build());
			totalCount += list.size();
		}
		long cost = System.currentTimeMillis() - t0;
		System.out.println("累计 Select 数量：" + totalCount);
		System.out.println("MapSearcher 耗时：" + cost + "ms");
	}

	@Test
	void testBeanSearcher() {
		System.out.println("BeanSearcher 执行 10000 次...");
		long t0 = System.currentTimeMillis();
		int totalCount = 0;
		for (int i = 0; i < 10000; i++) {
			List<Employee> list = beanSearcher.searchAll(Employee.class, MapUtils.builder()
					.field(Employee::getAge, 30)
					.build());
			totalCount += list.size();
		}
		long cost = System.currentTimeMillis() - t0;
		System.out.println("累计 Select 数量：" + totalCount);
		System.out.println("BeanSearcher 耗时：" + cost + "ms");
	}

	@Test
	void testMyBatisPlus() {
		System.out.println("MyBatisPlus 执行 10000 次 ...");
		long t0 = System.currentTimeMillis();
		int totalCount = 0;
		for (int i = 0; i < 10000; i++) {
			List<Employee> list = employeeMapper.selectList(new LambdaQueryWrapper<>(Employee.class).eq(Employee::getAge, 30));
			totalCount += list.size();
		}
		long cost = System.currentTimeMillis() - t0;
		System.out.println("累计 Select 数量：" + totalCount);
		System.out.println("MyBatisPlus 耗时：" + cost + "ms");
	}



}
