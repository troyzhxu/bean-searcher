package com.ejlchina.searcher;

import com.ejlchina.searcher.param.Operator;

import java.util.ArrayList;
import java.util.HashMap;
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
	 * 过滤检索参数列表
	 */
	private final List<FilterParam> filterParamList = new ArrayList<>();

	/**
	 * 排序字段（用于排序）
	 */
	private String sort;

	/**
	 * 排序方法：desc, asc（用于排序）
	 */
	private String order;

	/**
	 * 查询最大条数（用于分页）
	 */
	private Integer max = 10;

	/**
	 * 查询偏移条数（用于分页）
	 */
	private Long offset;


	private Long page;

	/**
	 * 需要求和的字段
	 */
	private String[] summaryFields;
	
	/**
	 * 需要查询总数
	 */
	private boolean shouldQueryTotal;
	
	/**
	 * 需要查询列表
	 */
	private boolean shouldQueryList;
	

	public SearchParam(Map<String, Object> paraMap, Integer max) {
		this.paraMap = paraMap;
		this.max = max;
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
	
	public void removeUselessFilterParam() {
		int size = filterParamList.size();
		for (int i = size - 1; i >= 0; i--) {
			FilterParam param = filterParamList.get(i);
			Operator op = param.getOperator();
			if (param.allValuesEmpty() && op != Operator.Empty
					&& op != Operator.NotEmpty) {
				filterParamList.remove(i);
			}
		}
	}

	public void addFilterParam(FilterParam filterParam) {
		filterParamList.add(filterParam);
	}

	public List<FilterParam> getFilterParamList() {
		return filterParamList;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public Long getPage() {
		return page;
	}

	public void setPage(Long page) {
		this.page = page;
	}

	public String[] getSummaryFields() {
		return summaryFields;
	}

	public void setSummaryFields(String[] summaryFields) {
		this.summaryFields = summaryFields;
	}

	public boolean isShouldQueryTotal() {
		return shouldQueryTotal;
	}

	public void setShouldQueryTotal(boolean shouldQueryTotal) {
		this.shouldQueryTotal = shouldQueryTotal;
	}

	public boolean isShouldQueryList() {
		return shouldQueryList;
	}

	public void setShouldQueryList(boolean shouldQueryList) {
		this.shouldQueryList = shouldQueryList;
	}

}
