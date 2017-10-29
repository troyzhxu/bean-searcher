package com.ejl.searcher.param;

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
	 * 过滤检索参数列表
	 */
	private List<FilterParam> filterParamList = new ArrayList<>();

	/**
	 * 虚拟参数值映射
	 */
	private Map<String, String> virtualParamMap = new HashMap<>();
	

	public SearchParam() {
	}
	
	public SearchParam(Integer max) {
		super();
		this.max = max;
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

	public Map<String, String> getVirtualParamMap() {
		return virtualParamMap;
	}

	public void putVirtualParam(String name, String value) {
		this.virtualParamMap.put(name, value);;
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

	@Override
	public String toString() {
		String str = "SearchParam: \n\tsort = " + sort + "\n\torder=" + order + "\n\tmax=" + max + "\n\toffset="
				+ offset + "\n\tfilterParamList:";
		for (FilterParam param : filterParamList) {
			str += "\n\t\t" + param.toString();
		}
		return str;
	}

}
