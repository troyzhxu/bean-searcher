package com.example.sbean;

import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.operator.Contain;
import com.ejlchina.searcher.operator.StartWith;
import com.example.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 带冗余后缀的实体类
 */
public class EmployeeVO extends BaseBean {

	@DbField(onlyOn = { StartWith.class, Contain.class })
	private String name;

	private Integer age;

	private Gender gender;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private LocalDateTime entryDate;

	public String getName() {
		return name;
	}

	public Integer getAge() {
		return age;
	}

	public Gender getGender() {
		return gender;
	}

	public LocalDateTime getEntryDate() {
		return entryDate;
	}

}
