package com.ejl.searcher;

import java.util.ArrayList;
import java.util.List;



/***
 * 检索结果
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 * */
public class SearchResult<T> {

	private Number totalCount = 0;
	
	private List<T> dataList = new ArrayList<>();
		
	
	public SearchResult() {
	}
	
	public SearchResult(Number totalCount) {
		super();
		this.totalCount = totalCount;
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
	
	
	public String toString() {
		String str = "totalCount = " + totalCount + "\n";
		for (T data: dataList) {
			str += "\t" + data.toString();
		}
		return str;
	}
	
}
