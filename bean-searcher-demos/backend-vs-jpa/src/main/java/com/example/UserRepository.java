package com.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 用户数据访问层（Spring Data JPA）。
 *
 * 对比 Bean Searcher：Bean Searcher 无需手写 Repository，检索/分页/排序/统计由框架自动完成；
 * 这里通过 JpaRepository + JpaSpecificationExecutor 获得基础 CRUD 与「按 Specification 动态查询」能力，
 * 但具体的过滤条件、排序、统计仍需在 Controller / Specification 中手动编写（见 UserController）。
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

}
