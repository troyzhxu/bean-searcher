package com.ejlchina.searcher.group;

import com.ejlchina.searcher.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Group 解析器
 * @author Troy.Zhou @ 2022-02-20
 * @since v3.5.0
 */
public class GroupResolver {

    static final Logger log = LoggerFactory.getLogger(GroupResolver.class);

    private char andKey = '+';

    private char orKey = '|';

    private int cacheSize = 100;

    private final Object lock = new Object();

    // LRU 缓存模型
    private final LinkedHashMap<String, Group<String>> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > cacheSize;
        }
    };

    public Group<String> resolve(String expr) {
        Group<String> gExpr;
        synchronized (lock) {
            gExpr = cache.get(expr);
        }
        if (gExpr == null) {
            gExpr = doResolve(expr);
            synchronized (lock) {
                cache.put(expr, gExpr);
            }
        }
        return gExpr;
    }

    protected Group<String> doResolve(String expr) {
        if (StringUtils.isBlank(expr)) {
            return new Group<>(Group.TYPE_RAW);
        }
        try {
            return createParser(expr).parse();
        } catch (Exception e) {
            log.warn("can not parse expr: " + expr);
            return new Group<>(Group.TYPE_RAW);
        }
    }

    public ExprParser createParser(String expr) {
        return new ExprParser(expr, andKey, orKey);
    }

    public char getAndKey() {
        return andKey;
    }

    public void setAndKey(char andKey) {
        this.andKey = andKey;
    }

    public char getOrKey() {
        return orKey;
    }

    public void setOrKey(char orKey) {
        this.orKey = orKey;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

}
