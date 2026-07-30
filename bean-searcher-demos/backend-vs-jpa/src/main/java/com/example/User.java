package com.example;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户检索实体（JPA 实现，对应 users 表 + dept 表关联）。
 *
 * 对比 Bean Searcher 版本：Bean Searcher 通过 @SearchBean / @DbField / @LabelFor 注解声明多表映射与枚举标签；
 * 这里用标准 JPA + Hibernate 映射：
 * - gender    : Gender 枚举，@Enumerated(STRING) 存储 'Male' / 'Female'，序列化输出与 Bean Searcher 枚举一致
 * - department: 通过 @ManyToOne(fetch=EAGER) 关联 dept 表，Hibernate 自动生成 LEFT JOIN dept ON users.dept_id = dept.id；
 *               getDepartment() 返回 dept.name，@JsonIgnore 隐藏 dept 关联对象，
 *               与 Bean Searcher（@SearchBean tables + @DbField("d.name")）和 MyBatis（显式 LEFT JOIN）的 SQL 一致
 * - genderName: 派生字段，取 gender.label（等价于 Bean Searcher 的 @LabelFor("gender")）
 * - entryDate : @JsonFormat 控制 JSON 输出格式（等价于 Bean Searcher 的 @Export(format=...)）
 *
 * 响应字段结构与 Bean Searcher / MyBatis 版完全一致：id, name, age, gender, department, entryDate, genderName
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

	@Id
	private Long id;

	private String name;

	private int age;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	/**
	 * 部门关联，EAGER 加载生成 LEFT JOIN dept，与外层 Specification/Criteria 查询中的 dept join 会合并。
	 * @JsonIgnore 隐藏关联对象，部门名通过 getDepartment() 暴露为字符串。
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "dept_id", insertable = false, updatable = false)
	@JsonIgnore
	private Department dept;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private LocalDateTime entryDate;

	/**
	 * 部门名，由关联实体 dept.name 派生，等价于 Bean Searcher 的 @DbField("d.name")。
	 */
	public String getDepartment() {
		return dept == null ? null : dept.getName();
	}

	/**
	 * 性别中文名，由枚举 label 派生，等价于 Bean Searcher 的 @LabelFor("gender")。
	 */
	public String getGenderName() {
		return gender == null ? null : gender.getLabel();
	}

}
