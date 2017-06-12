package com.ejl.searcher;

import java.util.ArrayList;
import java.util.List;



/***
 * @author Troy.Zhou @ 2017-03-20
 * 
 * 检索中间结果
 * 
 * */
public class SearchTmpResult {

	private Number totalCount;
	
	private List<SearchTmpData> tmpDataList = new ArrayList<>();
		
	public SearchTmpResult() {
	}
	
	public SearchTmpResult(Number totalCount) {
		super();
		this.totalCount = totalCount;
	}

	public SearchTmpResult(Number totalCount, List<SearchTmpData> tmpDataList) {
		this.totalCount = totalCount;
		this.tmpDataList = tmpDataList;
	}

	public Number getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Number totalCount) {
		this.totalCount = totalCount;
	}
	
	public List<SearchTmpData> getTmpDataList() {
		return tmpDataList;
	}

	public void setTmpDataList(List<SearchTmpData> tmpDataList) {
		this.tmpDataList = tmpDataList;
	}

	public void addTmpData(SearchTmpData data) {
		tmpDataList.add(data);
	}
	
}
