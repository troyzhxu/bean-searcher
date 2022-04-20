package com.ejlchina.searcher;

import com.ejlchina.searcher.group.Group;
import com.ejlchina.searcher.param.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 检索参数
 * 
 * @author Troy.Zhou @ 2017-03-21
 */
public class SearchParam {

	// 原始检索参数
	private final Map<String, Object> paraMap;

	// Fetch 类型
	private final FetchType fetchType;

	// 需要 Select 的字段
	private final List<String> fetchFields;

	// 字段参数组
	private final Group<List<FieldParam>> paramsGroup;

	// 排序参数
	private final List<OrderBy> orderBys = new ArrayList<>();

	// 分页参数
	private final Paging paging;

	public SearchParam(Map<String, Object> paraMap, FetchType fetchType, List<String> fetchFields,
					   Group<List<FieldParam>> paramsGroup, Paging paging) {
		this.paraMap = paraMap;
		this.fetchType = fetchType;
		this.fetchFields = fetchFields;
		this.paramsGroup = paramsGroup;
		this.paging = paging;
	}

	/**
	 * 获取原始参数
	 * @return 原始参数
	 */
	public Map<String, Object> getParaMap() {
		return paraMap;
	}

	public FetchType getFetchType() {
		return fetchType;
	}

	public List<String> getFetchFields() {
		return fetchFields;
	}

	public Group<List<FieldParam>> getParamsGroup() {
		return paramsGroup;
	}

	public Paging getPaging() {
		return paging;
	}

	public void addOrderBy(OrderBy orderBy) {
		orderBys.add(orderBy);
	}

	public List<OrderBy> getOrderBys() {
		return orderBys;
	}

}
