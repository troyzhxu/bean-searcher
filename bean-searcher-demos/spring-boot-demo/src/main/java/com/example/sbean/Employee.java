package com.example.sbean;

import java.util.Date;
import java.util.Map;

import com.ejlchina.searcher.bean.BeanAware;
import com.ejlchina.searcher.bean.BeanParaAware;
import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;
import com.ejlchina.searcher.param.Operator;
import com.fasterxml.jackson.annotation.JsonFormat;

@SearchBean(
		tables = "employee e, department d",
		joinCond = "e.department_id = d.id",
		autoMapTo = "e"							// 字段没使用 DbField 注解时，自动映射到 employee 表
)
public class Employee
		implements BeanAware, BeanParaAware    	// 这两接口 都是可选的
{

	private Long id;

	@DbField(onlyOn = Operator.Equal)
	private String name;

	private Integer age;

	@DbField("d.name")
	private String department;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private Date entryDate;

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

	/**
	 * BeanAware 接口的方法
	 */
	@Override
	public void afterAssembly() {
		System.out.println("id = " + id + ", name = " + name + ", age = " + age);
	}

	/**
	 * BeanParaAware 接口的方法
	 */
	@Override
	public void afterAssembly(Map<String, Object> paraMap) {
		System.out.println("paraMap" + paraMap);
	}

}
