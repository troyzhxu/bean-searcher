package com.ejlchina.searcher;

import com.ejlchina.searcher.param.FetchInfo;
import com.ejlchina.searcher.param.FieldParam;
import com.ejlchina.searcher.param.OrderParam;
import com.ejlchina.searcher.param.PageParam;

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
	private final FetchInfo fetchInfo;

	/**
	 * 过滤检索参数列表
	 */
	private final List<FieldParam> fieldParams;

	/**
	 * 分页参数
	 */
	private PageParam pageParam;

	/**
	 * 排序参数
	 */
	private OrderParam orderParam;


	public SearchParam(Map<String, Object> paraMap, FetchInfo fetchInfo, List<FieldParam> fieldParams) {
		this.paraMap = paraMap;
		this.fetchInfo = fetchInfo;
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

	public FetchInfo getFetchInfo() {
		return fetchInfo;
	}

	public PageParam getPageParam() {
		return pageParam;
	}

	public void setPageParam(PageParam pageParam) {
		this.pageParam = pageParam;
	}

	public OrderParam getOrderParam() {
		return orderParam;
	}

	public void setOrderParam(OrderParam orderParam) {
		this.orderParam = orderParam;
	}

}
