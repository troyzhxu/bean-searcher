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
public class SearchMapResult {

	
	private Number totalCount;
	
	private final List<Map<String, Object>> dataList = new ArrayList<>();

	private Number[] summaries;
	
	
	public SearchMapResult() {
	}

	public SearchMapResult(Number totalCount) {
		this.totalCount = totalCount;
	}

	public Number getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Number totalCount) {
		this.totalCount = totalCount;
	}
	
	public List<Map<String, Object>> getDataList() {
		return dataList;
	}

	public void addData(Map<String, Object> data) {
		dataList.add(data);
	}
	
	public Number[] getSummaries() {
		return summaries;
	}

	public void setSummaries(Number[] summaries) {
		if (summaries != null) {
			for (int i = 0; i < summaries.length; i++) {
				if (summaries[i] == null) {
					summaries[i] = 0;
				}
			}
			this.summaries = summaries;
		} else {
			this.summaries = new Number[] {};
		}
	}
	
}
