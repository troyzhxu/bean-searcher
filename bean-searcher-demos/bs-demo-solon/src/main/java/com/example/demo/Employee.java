package com.example.demo;


import cn.zhxu.bs.bean.DbField;
import cn.zhxu.bs.bean.SearchBean;
import cn.zhxu.bs.operator.Contain;
import cn.zhxu.bs.operator.StartWith;
import org.noear.snack.annotation.ONodeAttr;

import java.time.LocalDateTime;

@SearchBean(
        tables = "employee e, department d",
        where = "e.department_id = d.id",
        autoMapTo = "e"		// 字段没使用 DbField 注解时，自动映射到 employee 表
)
public class Employee {

    @DbField(onlyOn = { StartWith.class, Contain.class })
    private String name;

    private Integer age;

    private Gender gender;

    @DbField("d.name")
    private String department;

    @ONodeAttr(format = "yyyy-MM-dd HH:mm")
    private LocalDateTime entryDate;

}
