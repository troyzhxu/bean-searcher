package com.ejl.searcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 检索中间结果
 * 
 * */
public class SearchTmpResult {

	private Number totalCount;
	
	private List<Map<String, Object>> tmpDataList = new ArrayList<>();
		
	public SearchTmpResult() {
	}
	
	public SearchTmpResult(Number totalCount) {
		super();
		this.totalCount = totalCount;
	}

	public SearchTmpResult(Number totalCount, List<Map<String, Object>> tmpDataList) {
		this.totalCount = totalCount;
		this.tmpDataList = tmpDataList;
	}

	public Number getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Number totalCount) {
		this.totalCount = totalCount;
	}
	
	public List<Map<String, Object>> getTmpDataList() {
		return tmpDataList;
	}

	public void setTmpDataList(List<Map<String, Object>> tmpDataList) {
		this.tmpDataList = tmpDataList;
	}

	public void addTmpData(Map<String, Object> data) {
		tmpDataList.add(data);
	}
	
}
