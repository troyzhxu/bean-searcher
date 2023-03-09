package com.example.demo;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.MapSearcher;
import cn.zhxu.bs.util.MapUtils;
import com.example.demo.entity.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DemoApplicationTests {

	@Autowired
	private BeanSearcher beanSearcher;

	@Autowired
	private MapSearcher mapSearcher;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Test
	@Order(1)
	void warmup() {
		System.out.println("热身...");
		System.out.println("Bean Searcher 与 Data Jpa 各执行 1000 次...");
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			beanSearcher.searchAll(Employee.class, MapUtils.builder()
					.field(Employee::getAge, 30)
					.build());
			mapSearcher.searchAll(Employee.class, MapUtils.builder()
					.field(Employee::getAge, 30)
					.build());
			employeeRepository.findAllByAge(30);
		}
		long cost = System.currentTimeMillis() - t0;
		System.out.println("热身完毕，耗时：" + cost + "ms");
	}


	@Test
	@Order(2)
	void testMapSearcher() {
		System.out.println("MapSearcher 执行 1000 次...");
		long t0 = System.currentTimeMillis();
		int totalCount = 0;
		for (int i = 0; i < 1000; i++) {
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
	@Order(3)
	void testDataJpa() {
		System.out.println("Data Jpa 执行 1000 次 ...");
		long t0 = System.currentTimeMillis();
		int totalCount = 0;
		for (int i = 0; i < 1000; i++) {
			List<Employee> list = employeeRepository.findAllByAge(30);
			totalCount += list.size();
		}
		long cost = System.currentTimeMillis() - t0;
		System.out.println("累计 Select 数量：" + totalCount);
		System.out.println("Data Jpa 耗时：" + cost + "ms");
	}

	@Test
	@Order(4)
	void testBeanSearcher() {
		System.out.println("BeanSearcher 执行 1000 次...");
		long t0 = System.currentTimeMillis();
		int totalCount = 0;
		for (int i = 0; i < 1000; i++) {
			List<Employee> list = beanSearcher.searchAll(Employee.class, MapUtils.builder()
					.field(Employee::getAge, 30)
					.build());
			totalCount += list.size();
		}
		long cost = System.currentTimeMillis() - t0;
		System.out.println("累计 Select 数量：" + totalCount);
		System.out.println("BeanSearcher 耗时：" + cost + "ms");
	}

}
