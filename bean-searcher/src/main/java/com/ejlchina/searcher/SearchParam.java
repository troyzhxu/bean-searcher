package com.ejlchina.searcher;

import com.ejlchina.searcher.param.FetchType;
import com.ejlchina.searcher.param.FieldParam;
import com.ejlchina.searcher.param.OrderParam;
import com.ejlchina.searcher.param.Paging;

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
	 * Fetch 信息
	 */
	private final FetchType fetchType;

	/**
	 * 过滤检索参数列表
	 */
	private final List<FieldParam> fieldParams;

	/**
	 * 分页参数
	 */
	private Paging paging;

	/**
	 * 排序参数
	 */
	private OrderParam orderParam;


	public SearchParam(Map<String, Object> paraMap, FetchType fetchType, List<FieldParam> fieldParams) {
		this.paraMap = paraMap;
		this.fetchType = fetchType;
		this.fieldParams = fieldParams;
	}

	/**
	 * 获取原始参数
	 */
	public Map<String, Object> getParaMap() {
		return paraMap;
	}

	/**
	 * 获取原始参数值
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

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public OrderParam getOrderParam() {
		return orderParam;
	}

	public void setOrderParam(OrderParam orderParam) {
		this.orderParam = orderParam;
	}

}
