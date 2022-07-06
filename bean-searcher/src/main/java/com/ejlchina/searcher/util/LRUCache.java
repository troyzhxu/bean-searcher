package com.ejlchina.searcher.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LRU 缓存
 * @param <T> 泛型
 * @author Troy.Zhou @ 2022-02-22
 * @since v3.5.0
 */
public class LRUCache<T> extends LinkedHashMap<String, T> implements Cache<T> {

    private int maxCacheCount;

    private final Lock lock = new ReentrantLock();

    public LRUCache(int maxCacheCount) {
        this.maxCacheCount = maxCacheCount;
    }

    @Override
    public T get(String key) {
        lock.lock();
        try {
            return super.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void cache(String key, T value) {
        lock.lock();
        try {
            put(key, value);
        } finally {
            lock.unlock();
        }
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
