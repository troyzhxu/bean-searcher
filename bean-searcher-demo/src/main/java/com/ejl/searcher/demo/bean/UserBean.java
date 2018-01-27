package com.ejl.searcher.demo.bean;

import java.sql.Timestamp;

import com.ejl.searcher.bean.DbField;
import com.ejl.searcher.bean.SearchBean;


@SearchBean(tables = "users u", joinCond="u.admin_user = 1")
public class UserBean {
	
	@DbField("u.id")
	private Long id;
	
	@DbField("u.name")
	private String name;
	
	@DbField("u.phone")
	private String phone;
	
	@DbField("u.created_at")
	private Timestamp createdAt;


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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

}
