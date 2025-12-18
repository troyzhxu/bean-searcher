package com.example.demo;

import cn.zhxu.bs.bean.DbField;
import cn.zhxu.bs.bean.DbType;
import cn.zhxu.bs.bean.SearchBean;
import cn.zhxu.bs.ex.Export;
import cn.zhxu.bs.operator.Contain;
import cn.zhxu.bs.operator.StartWith;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@SearchBean(
	tables = "employee e, department d",
	where = "e.department_id = d.id",
	autoMapTo = "e",							// 字段没使用 DbField 注解时，自动映射到 employee 表
	orderBy = "e.entry_date desc",
	maxSize = 1000
)
public class Employee {

    @Export(name = "ID")
	private long id;

    @Export(name = "姓名")
	@DbField(onlyOn = { StartWith.class, Contain.class })
	private String name;

    @Export(name = "年龄")
	private Integer age;

    @Export(name = "性别")
	private Gender gender;

    @Export(name = "部门")
	@DbField("d.name")
	private String department;

    @Export(name = "入职时间", format = "yyyy-MM-dd HH:mm")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private LocalDateTime entryDate;

    // 测试转换表达式
    @Export(name = "领导_员工年龄", expr = "@.leader + '_' + age")
	@DbField(mapTo = "d", type = DbType.JSON)
	private DeptAttr attrs;

	public long getId() {
		return id;
	}

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

	public DeptAttr getAttrs() {
		return attrs;
	}

}
