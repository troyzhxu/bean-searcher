package com.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 部门实体，映射 dept 表。
 * User 通过 @ManyToOne(fetch=EAGER) 关联此实体，
 * Hibernate 自动生成 LEFT JOIN dept ON users.dept_id = dept.id，
 * 与 Bean Searcher / MyBatis 的联表 SQL 一致。
 */
@Getter
@Setter
@Entity
@Table(name = "dept")
public class Department {

	@Id
	private Long id;

	private String name;

	private LocalDateTime createDate;

}
