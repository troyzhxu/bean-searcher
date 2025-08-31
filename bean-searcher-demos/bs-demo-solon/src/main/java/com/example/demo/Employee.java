package com.example.demo;

import cn.zhxu.bs.bean.DbField;
import cn.zhxu.bs.bean.SearchBean;
import cn.zhxu.bs.ex.Export;
import cn.zhxu.bs.label.LabelFor;
import cn.zhxu.bs.operator.Contain;
import cn.zhxu.bs.operator.StartWith;
import org.noear.snack.annotation.ONodeAttr;

import java.time.LocalDateTime;

@SearchBean(
    tables = "employee e, department d",
    where = "e.department_id = d.id",
    autoMapTo = "e",
    maxSize = 1000
)
public class Employee {

    @Export(name = "ID")
    private int id;

    @Export(name = "姓名")
    @DbField(onlyOn = { StartWith.class, Contain.class })
    private String name;

    @Export(name = "年龄")
    private int age;

    private Gender gender;

    @Export(name = "性别")
    @LabelFor("gender")
    private String genderName;

    @Export(name = "部门")
    @DbField("d.name")
    private String department;

    @Export(name = "入职时间", format = "yyyy-MM-dd HH:mm")
    @ONodeAttr(format = "yyyy-MM-dd HH:mm")
    private LocalDateTime entryDate;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Gender getGender() {
        return gender;
    }

    public String getGenderName() {
        return genderName;
    }

    public String getDepartment() {
        return department;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

}
