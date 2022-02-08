package com.ejlchina.searcher;

import com.ejlchina.searcher.param.FetchType;
import com.ejlchina.searcher.param.FieldParam;
import com.ejlchina.searcher.param.OrderBy;
import com.ejlchina.searcher.param.Paging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 检索参数
 * 
 * @author Troy.Zhou @ 2017-03-21
 */
public class SearchParam {

	/**
	 * 原始检索参数
	 */
	private final Map<String, Object> paraMap;

	/**
	 * Fetch 类型
	 */
	private final FetchType fetchType;

	/**
	 * 需要 Select 的字段
	 */
	private final List<String> fetchFields;

	/**
	 * 过滤检索参数列表
	 */
	private final List<FieldParam> fieldParams;

	/**
	 * 排序参数
	 */
	private final List<OrderBy> orderBys = new ArrayList<>();

	/**
	 * 分页参数
	 */
	private Paging paging;


	public SearchParam(Map<String, Object> paraMap, FetchType fetchType, List<String> fetchFields, List<FieldParam> fieldParams) {
		this.paraMap = paraMap;
		this.fetchType = fetchType;
		this.fetchFields = fetchFields;
		this.fieldParams = fieldParams;
	}

	/**
	 * 获取原始参数
	 * @return 原始参数
	 */
	public Map<String, Object> getParaMap() {
		return paraMap;
	}

	/**
	 * 获取原始参数值
	 * @param key 参数名
	 * @return 参数值
	 */
	public Object getPara(String key) {
		return paraMap.get(key);
	}

	public List<FieldParam> getFieldParams() {
		return fieldParams;
	}

	public FetchType getFetchType() {
		return fetchType;
	}

	public List<String> getFetchFields() {
		return fetchFields;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public void addOrderBy(OrderBy orderBy) {
		orderBys.add(orderBy);
	}

	public List<OrderBy> getOrderBys() {
		return orderBys;
	}

}
