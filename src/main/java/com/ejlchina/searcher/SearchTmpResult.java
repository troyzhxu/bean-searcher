package com.ejlchina.searcher;

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

	private Number[] summaries;
	
	
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
	
	public Number[] getSummaries() {
		return summaries;
	}

	public void setSummaries(Number[] summaries) {
		this.summaries = summaries;
	}
	
}
