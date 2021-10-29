package com.demo.sbean

import com.ejlchina.searcher.bean.DbField
import com.ejlchina.searcher.bean.SearchBean
import com.fasterxml.jackson.annotation.JsonFormat


@SearchBean(tables = "employee e, department d", joinCond = "e.department_id = d.id")
class EmployeeBean {

    @DbField("e.id")
    Long id;

    @DbField("e.name")
    String name;

    @DbField("e.age")
    Integer age;

    @DbField("d.name")
    String department;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DbField("e.entry_date")
    Date entryDate;

}
