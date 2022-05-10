package com.example.demo;

import com.ejlchina.searcher.BeanSearcher;
import com.example.demo.entity.Employee;
import com.example.demo.mapper.EmployeeMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private BeanSearcher beanSearcher;

	@Autowired
	private EmployeeMapper employeeMapper;

	@Test
	void warmup() {
		System.out.println("热身...");
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			employeeMapper.selectList(null);
			beanSearcher.searchAll(Employee.class, null);
		}
		long cost = System.currentTimeMillis() - t0;
		System.out.println("热身完毕，耗时：" + cost + "ms");
	}

	@Test
	void testBeanSearcher() {
		System.out.println("BeanSearcher 执行 1000 次...");
		long t0 = System.currentTimeMillis();
		int totalCount = 0;
		for (int i = 0; i < 1000; i++) {
			List<Employee> list = beanSearcher.searchAll(Employee.class, null);
			totalCount += list.size();
		}
		long cost = System.currentTimeMillis() - t0;
		System.out.println("累计 Select 数量：" + totalCount);
		System.out.println("BeanSearcher 耗时：" + cost + "ms");
	}

	@Test
	void testMyBatisPlus() {
		System.out.println("MyBatisPlus 执行 1000 次 ...");
		long t0 = System.currentTimeMillis();
		int totalCount = 0;
		for (int i = 0; i < 1000; i++) {
			List<Employee> list = employeeMapper.selectList(null);
			totalCount += list.size();
		}
		long cost = System.currentTimeMillis() - t0;
		System.out.println("累计 Select 数量：" + totalCount);
		System.out.println("MyBatisPlus 耗时：" + cost + "ms");
	}



}
