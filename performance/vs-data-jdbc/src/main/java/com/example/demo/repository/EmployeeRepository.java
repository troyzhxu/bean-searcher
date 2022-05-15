package com.example.demo.repository;


import com.example.demo.entity.Employee;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Integer> {

    List<Employee> findAllByAge(int age);

}
