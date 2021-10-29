package com.ejlchina.searcher;

import java.util.ArrayList;
import java.util.List;



/***
 * 检索结果
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 * */
public class SearchResult<T> {

	private Number totalCount;
	
	private final List<T> dataList = new ArrayList<>();
	
	private Number[] summaries;
	
	
	public SearchResult(Number totalCount, Number[] summaries) {
		this.totalCount = totalCount;
		this.summaries = summaries;
	}

	
	public Number getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Number totalCount) {
		this.totalCount = totalCount;
	}

	public List<T> getDataList() {
		return dataList;
	}
	
	public void addData(T data) {
		dataList.add(data);
	}
	
	public Number[] getSummaries() {
		return summaries;
	}

	public void setSummaries(Number[] summaries) {
		this.summaries = summaries;
	}


	public String toString() {
		StringBuilder str = new StringBuilder("totalCount = " + totalCount + "\n");
		for (T data: dataList) {
			str.append("\t").append(data.toString());
		}
		return str.toString();
	}
	
}
