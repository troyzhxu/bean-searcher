package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.util.BoolGroup;
import com.ejlchina.searcher.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * BoolGroup 解析器
 * @author Troy.Zhou @ 2022-02-20
 * @since v3.5.0
 */
public class GroupExprResolver {

    static final Logger log = LoggerFactory.getLogger(GroupExprResolver.class);

    private char andKey = '*';

    private char orKey = '+';

    private int cacheSize = 100;

    private final Object lock = new Object();

    private final LinkedHashMap<String, BoolGroup<String>> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > cacheSize;
        }
    };

    public BoolGroup<String> resolve(String expr) {
        BoolGroup<String> gExpr;
        synchronized (lock) {
            gExpr = cache.get(expr);
        }
        if (gExpr != null) {
            return gExpr;
        }
        gExpr = doResolve(expr);
        synchronized (lock) {
            return cache.put(expr, gExpr);
        }
    }

    protected BoolGroup<String> doResolve(String expr) {
        if (StringUtils.isBlank(expr)) {
            return new BoolGroup<>(BoolGroup.TYPE_RAW);
        }
        try {
            return createParser(expr).parse();
        } catch (Exception e) {
            log.warn("can not parse expr: " + expr);
            return new BoolGroup<>(BoolGroup.TYPE_RAW);
        }
    }

    protected GroupExprParser createParser(String expr) {
        return new GroupExprParser(expr, andKey, orKey);
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
