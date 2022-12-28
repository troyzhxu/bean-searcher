package com.example.sbean;

import cn.zhxu.bs.bean.DbField;
import cn.zhxu.bs.bean.SearchBean;
import cn.zhxu.bs.operator.Contain;
import cn.zhxu.bs.operator.StartWith;
import com.example.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@SearchBean(
		tables = "employee e, department d",
		joinCond = "e.department_id = d.id",
		autoMapTo = "e"							// 字段没使用 DbField 注解时，自动映射到 employee 表
)
public class Employee extends BaseBean {

	@DbField(onlyOn = { StartWith.class, Contain.class })
	private String name;

	private Integer age;

	private Gender gender;

	@DbField("d.name")
	private String department;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private LocalDateTime entryDate;

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

}
