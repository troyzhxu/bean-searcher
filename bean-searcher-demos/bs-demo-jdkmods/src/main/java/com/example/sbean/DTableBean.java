package com.example.sbean;

import cn.zhxu.bs.bean.DbField;
import cn.zhxu.bs.bean.SearchBean;

@SearchBean(tables = ":table:")
public class DTableBean {

    @DbField("id")
    private Long id;

    @DbField("name")
    private String name;

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

}
