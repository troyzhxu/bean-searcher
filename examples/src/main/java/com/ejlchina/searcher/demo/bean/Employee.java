package com.ejlchina.searcher.demo.bean;

import java.util.Date;

import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;
import com.fasterxml.jackson.annotation.JsonFormat;

@SearchBean(tables = "employee e, department d", joinCond = "e.department_id = d.id")
public class Employee {

	@DbField("e.id")
	private Long id;

	@DbField("e.name")
	private String name;
	
	@DbField("e.age")
	private Integer age;

	@DbField("d.name")
	private String department;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone = "GMT+8")
	@DbField("e.entry_date")
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

}
