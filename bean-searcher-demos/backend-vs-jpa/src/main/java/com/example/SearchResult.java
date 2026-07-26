package com.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 检索接口响应结构。
 *
 * 与前端（frontend-vue）约定的契约保持一致：
 * - dataList   : 当前页数据（对应 Bean Searcher 的 list 字段）
 * - totalCount : 总条数（对应 Bean Searcher 的 totalCount 字段）
 * - summaries  : 统计结果数组，下标 0 为年龄求和（对应 Bean Searcher search(User.class, User::getAge) 的 summaries）
 */
@Getter
@Setter
@AllArgsConstructor
public class SearchResult {

	private List<User> dataList;

	private long totalCount;

	private List<Number> summaries;

}
