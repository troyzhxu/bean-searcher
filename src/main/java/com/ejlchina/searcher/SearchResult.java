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

	private Number totalCount = 0;
	
	private Number max;
	private Number offset;
	private Number page;
	private Number totalPage;
	
	private List<T> dataList = new ArrayList<>();
	
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
	
	public Number getMax() {
		return max;
	}

	public void setMax(Number max) {
		this.max = max;
	}

	public Number getOffset() {
		return offset;
	}

	public void setOffset(Number offset) {
		this.offset = offset;
	}

	public Number getPage() {
		return page;
	}

	public void setPage(Number page) {
		this.page = page;
	}

	public Number getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Number totalPage) {
		this.totalPage = totalPage;
	}

	
	public Number[] getSummaries() {
		return summaries;
	}


	public void setSummaries(Number[] summaries) {
		this.summaries = summaries;
	}


	public String toString() {
		String str = "totalCount = " + totalCount + "\n";
		for (T data: dataList) {
			str += "\t" + data.toString();
		}
		return str;
	}
	
}
