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
	
	private final List<T> dataList;
	
	private Number[] summaries;

	public SearchResult() {
		this(0, new Number[]{});
	}

	public SearchResult(Number totalCount) {
		this(totalCount, new Number[]{});
	}
	
	public SearchResult(Number totalCount, Number[] summaries) {
		this(new ArrayList<>());
		this.totalCount = totalCount;
		this.summaries = summaries;
	}

	public SearchResult(List<T> dataList) {
		this.dataList = dataList;
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

	public String toString() {
		StringBuilder str = new StringBuilder("totalCount = " + totalCount + "\n");
		for (T data: dataList) {
			str.append("\t").append(data.toString());
		}
		return str.toString();
	}
	
}
