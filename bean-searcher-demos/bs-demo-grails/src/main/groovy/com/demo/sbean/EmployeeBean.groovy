package com.demo.sbean

import cn.zhxu.bs.bean.DbField
import cn.zhxu.bs.bean.SearchBean
import com.fasterxml.jackson.annotation.JsonFormat


@SearchBean(
    tables = "employee e, department d",
    where = "e.department_id = d.id",
    autoMapTo = "e"
)
class EmployeeBean {

    Long id;

    String name;

    Integer age;

    @DbField("d.name")
    String department;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone = "GMT+8")
    Date entryDate;

}
