package com.example.demo;

import com.ejlchina.searcher.BeanSearcher;
import com.example.demo.entity.Employee;
import com.example.demo.mapper.EmployeeMapper;
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
	void testBeanSearcher() {
		long t0 = System.nanoTime();
		List<Employee> list = beanSearcher.searchList(Employee.class, null);
		long cost = System.nanoTime() - t0;
		System.out.println("查出数量："+ list.size());
		System.out.println("BeanSearcher 耗时：" + cost);
	}

	@Test
	void testEmployeeMapper() {
		long t0 = System.nanoTime();
		List<Employee> list = employeeMapper.selectList(null);
		long cost = System.nanoTime() - t0;
		System.out.println("查出数量："+ list.size());
		System.out.println("MyBatisPlus 耗时：" + cost);
	}

}
