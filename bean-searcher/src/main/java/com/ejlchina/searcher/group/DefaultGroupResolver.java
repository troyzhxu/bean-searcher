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
public class DefaultGroupResolver implements GroupResolver {

    static final Logger log = LoggerFactory.getLogger(DefaultGroupResolver.class);

    static final Group<String> DEFAULT_RAW_GROUP = new Group<>(Group.TYPE_RAW);

    private final Object lock = new Object();

    // LRU 缓存模型
    private LRUCache<Group<String>> cache = new LRUCache<>(50);

    // 是否启用
    private boolean enabled = true;

    private ExprParser.Factory parserFactory = new DefaultParserFactory();

    @Override
    public Group<String> resolve(String gExpr) {
        if (enabled) {
            Group<String> group;
            synchronized (lock) {
                group = cache.get(gExpr);
            }
            if (group == null) {
                group = doResolve(gExpr);
                synchronized (lock) {
                    cache.put(gExpr, group);
                }
            }
            return group;
        }
        return DEFAULT_RAW_GROUP;
    }

    protected Group<String> doResolve(String expr) {
        if (StringUtils.isBlank(expr)) {
            return DEFAULT_RAW_GROUP;
        }
        try {
            return parserFactory.create(expr).parse();
        } catch (Exception e) {
            log.warn("Can not parse expr: [{}], fallback to DEFAULT_RAW_GROUP", expr);
        }
        return DEFAULT_RAW_GROUP;
    }

    @Override
    public ExprParser.Factory getParserFactory() {
        return parserFactory;
    }

    public void setParserFactory(ExprParser.Factory parserFactory) {
        this.parserFactory = parserFactory;
    }

    public LRUCache<Group<String>> getCache() {
        return cache;
    }

    public void setCache(LRUCache<Group<String>> cache) {
        this.cache = Objects.requireNonNull(cache);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
