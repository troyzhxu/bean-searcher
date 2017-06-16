package com.ejl.searcher.example.spring.bean;

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
	private Timestamp created_at;

	@DbField("u.md5password")
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

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

	public Timestamp getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Timestamp created_at) {
		this.created_at = created_at;
	}
	
}
