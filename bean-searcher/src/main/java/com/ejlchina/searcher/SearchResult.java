package com.ejlchina.searcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/***
 * 检索结果
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 * */
public class SearchResult<T> {

    private static final Number[] EMPTY_SUMMARIES = new Number[]{};

    private Number totalCount = 0;
    
    private final List<T> dataList;
    
    private Number[] summaries = EMPTY_SUMMARIES;

    public SearchResult() {
        this(0, EMPTY_SUMMARIES);
    }

    public SearchResult(Number totalCount) {
        this(totalCount, EMPTY_SUMMARIES);
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
        this.totalCount = Objects.requireNonNull(totalCount);
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
        this.summaries = Objects.requireNonNull(summaries);
    }

    public String toString() {
        StringBuilder str = new StringBuilder("totalCount = " + totalCount + "\n");
        for (T data: dataList) {
            str.append("\t").append(data.toString());
        }
        return str.toString();
    }
    
}
