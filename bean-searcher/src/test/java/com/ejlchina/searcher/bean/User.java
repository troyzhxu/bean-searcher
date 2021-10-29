package com.ejlchina.searcher.bean;

import java.util.Date;

import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;

@SearchBean(tables = "user")
public class User {

	
	@DbField("id")
	private Long id; 
	
	@DbField("name")
	private String name;
	
	@DbField("age")
	private Integer age;

	@DbField("date_created")
	private Date dateCreated;
	
	
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

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
}
