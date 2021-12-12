package com.example.sbean;

import com.ejlchina.searcher.bean.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.Map;

@SearchBean(
		tables = "employee e, department d",
		joinCond = "e.department_id = d.id",
		autoMapTo = "e"							// 字段没使用 DbField 注解时，自动映射到 employee 表
)
public class Employee
		implements BeanAware, ParamAware        // 这两接口 都是可选的
{

	public static final int serialID = 1233;

	// 自动映射到 "e.id"
	private Long id;

	@DbField
	private String name;

	// 自动映射到 "e.age"
	private Integer age;

	@DbField("d.name")
	private String department;

	// 自动映射到 "e.entry_date"
	@JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private Date entryDate;

	// 该字段不会映射到数据表
	@DbIgnore
	private String ignoreField = "ignore";

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public String getIgnoreField() {
		return ignoreField;
	}

	public void setIgnoreField(String ignoreField) {
		this.ignoreField = ignoreField;
	}

	/**
	 * BeanAware 接口的方法
	 */
	@Override
	public void afterAssembly() {
		System.out.println("id = " + id + ", name = " + name + ", age = " + age + ", ignoreField = " + ignoreField);
	}

	/**
	 * ParamAware 接口的方法
	 */
	@Override
	public void afterAssembly(Map<String, Object> paraMap) {
		System.out.println("paraMap = " + paraMap);
	}

}
