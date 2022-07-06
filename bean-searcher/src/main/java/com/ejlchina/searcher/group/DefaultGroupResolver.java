package com.ejlchina.searcher.group;

import com.ejlchina.searcher.util.Cache;
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

    // LRU 缓存模型（因为逻辑表达式可能是前端传来的，具有无限可能，不能来者不拒）
    private Cache<Group<String>> cache = new LRUCache<>(50);

    // 是否启用
    private boolean enabled = true;

    // 表达式最大允许长度
    private int maxExprLength = 50;

    private ExprParser.Factory parserFactory = new DefaultParserFactory();

    @Override
    public Group<String> resolve(String gExpr) {
        if (StringUtils.isBlank(gExpr)) {
            return DEFAULT_RAW_GROUP;
        }
        if (gExpr.length() > maxExprLength) {
            throw new IllegalArgumentException("gExpr is too long: " + gExpr.length() + ", max allowed length is " + maxExprLength);
        }
        if (enabled) {
            Group<String> group = cache.get(gExpr);
            if (group == null) {
                group = doResolve(gExpr);
                cache.cache(gExpr, group);
            }
            return group;
        }
        return DEFAULT_RAW_GROUP;
    }

    protected Group<String> doResolve(String expr) {
        try {
            return parserFactory.create(expr).parse();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public ExprParser.Factory getParserFactory() {
        return parserFactory;
    }

    public void setParserFactory(ExprParser.Factory parserFactory) {
        this.parserFactory = parserFactory;
    }

    public Cache<Group<String>> getCache() {
        return cache;
    }

    public void setCache(Cache<Group<String>> cache) {
        this.cache = Objects.requireNonNull(cache);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxExprLength() {
        return maxExprLength;
    }

    public void setMaxExprLength(int maxExprLength) {
        this.maxExprLength = maxExprLength;
    }

}
