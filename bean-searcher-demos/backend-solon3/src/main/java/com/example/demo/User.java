package com.example.demo;

import cn.zhxu.bs.bean.DbField;
import cn.zhxu.bs.bean.SearchBean;
import cn.zhxu.bs.ex.Export;
import cn.zhxu.bs.label.LabelFor;
import lombok.Getter;
import lombok.Setter;
import org.noear.snack.annotation.ONodeAttr;

import java.time.LocalDateTime;

@Getter
@Setter
@SearchBean(tables = "users u, dept d", where = "u.dept_id = d.id", autoMapTo = "u")
public class User {

	@Export(name = "ID")
	private long id;

	@Export(name = "姓名")
	private String name;

	@Export(name = "年龄")
	private int age;

	private Gender gender;

	@LabelFor("gender")
	@Export(name = "性别")
	private String genderName;

	@DbField("d.name")
	@Export(name = "部门")
	private String department;

	@Export(name = "入职时间", format = "yyyy-MM-dd HH:mm")
	@ONodeAttr(format = "yyyy-MM-dd HH:mm")
	private LocalDateTime entryDate;

}
