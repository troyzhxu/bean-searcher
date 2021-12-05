package com.example.sbean;

import com.ejlchina.searcher.bean.*;
import com.example.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

@SearchBean(
		tables = "employee e, department d",
		joinCond = "e.department_id = d.id",
		autoMapTo = "e"							// 字段没使用 DbField 注解时，自动映射到 employee 表
)
public class Employee extends BaseBean			// v3.2 开始支持实体类继承 与 省略 setter 方法
		implements BeanAware, ParamAware    	// 这两接口 都是可选的
{

	@DbField
	private String name;

	// 自动映射到 "e.age"
	private Integer age;

	// 枚举字段
	// 自动映射到 "e.gender"
	private Gender gender;

	@DbField("d.name")
	private String department;

	// 自动映射到 "e.entry_date"
	@JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private LocalDateTime entryDate;

	// 该字段不会映射到数据表
	@DbIgnore
	private String ignoreField = "ignore";

	public String getName() {
		return name;
	}

	public Integer getAge() {
		return age;
	}

	public Gender getGender() {
		return gender;
	}

	public String getDepartment() {
		return department;
	}

	public LocalDateTime getEntryDate() {
		return entryDate;
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
//		System.out.println("id = " + id + ", name = " + name + ", age = " + age + ", ignoreField = " + ignoreField);
	}

	/**
	 * ParamAware 接口的方法
	 */
	@Override
	public void afterAssembly(Map<String, Object> paraMap) {
//		System.out.println("paraMap = " + paraMap);
	}

}
