package com.ejlchina.searcher;

import java.util.ArrayList;
import java.util.List;

/**
 * 检索结果
 * @author Troy.Zhou @ 2017-03-20
 * */
public class SearchResult<T> {

    public static final Number[] EMPTY_SUMMARIES = new Number[]{};

    private final Number totalCount;
    
    private final List<T> dataList;
    
    private final Number[] summaries;

    public SearchResult(Number totalCount, int pageSize, Number[] summaries) {
        int size = Math.min(totalCount.intValue(), pageSize);
        this.dataList = new ArrayList<>(size);
        this.totalCount = totalCount;
        this.summaries = summaries;
    }

    public Number getTotalCount() {
        return totalCount;
    }

    public List<T> getDataList() {
        return dataList;
    }
    
    public Number[] getSummaries() {
        return summaries;
    }

    public String toString() {
        StringBuilder str = new StringBuilder("totalCount = " + totalCount + "\n");
        for (T data: dataList) {
            str.append("\t").append(data.toString());
        }
        return str.toString();
    }
    
}
