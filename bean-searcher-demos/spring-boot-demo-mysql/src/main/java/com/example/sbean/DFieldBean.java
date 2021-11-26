package com.example.sbean;

import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;

@SearchBean(tables = "employee")
public class DFieldBean {

    @DbField("id")
    private Long id;

    @DbField(":fieldName:")
    private String field;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
