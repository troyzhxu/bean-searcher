package com.ejlchina.searcher.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU 缓存
 * @param <T> 泛型
 * @author Troy.Zhou @ 2022-02-22
 * @since v3.5.0
 */
public class LRUCache<T> extends LinkedHashMap<String, T> {

    private int maxCacheCount;

    public LRUCache(int maxCacheCount) {
        this.maxCacheCount = maxCacheCount;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return this.size() > maxCacheCount;
    }

    public int getMaxCacheCount() {
        return maxCacheCount;
    }

    public void setMaxCacheCount(int maxCacheCount) {
        this.maxCacheCount = maxCacheCount;
    }

}
