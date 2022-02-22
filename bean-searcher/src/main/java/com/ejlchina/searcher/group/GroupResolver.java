package com.ejlchina.searcher.group;

import com.ejlchina.searcher.util.LRUCache;
import com.ejlchina.searcher.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Group 解析器
 * @author Troy.Zhou @ 2022-02-20
 * @since v3.5.0
 */
public class GroupResolver {

    static final Logger log = LoggerFactory.getLogger(GroupResolver.class);

    private char andKey = '&';

    private char orKey = '|';

    private final Object lock = new Object();

    // LRU 缓存模型
    private LRUCache<Group<String>> cache = new LRUCache<>(100);

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

    public LRUCache<Group<String>> getCache() {
        return cache;
    }

    public void setCache(LRUCache<Group<String>> cache) {
        this.cache = Objects.requireNonNull(cache);
    }

}
