package cn.zhxu.bs.dialect;

import cn.zhxu.bs.SearchException;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.param.Paging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态方言，当同一个项目中使用到多种不同数据库时使用
 * @author Troy.Zhou
 * @since v4.1.0
 */
public class DynamicDialect implements Dialect {

    private static final ThreadLocal<String> holder = new ThreadLocal<>();

    private final Map<String, Dialect> dialectMap = new ConcurrentHashMap<>();

    private String defaultKey = "default";

    /**
     * 设置当前方言
     * @param key 键
     */
    public static void current(String key) {
        holder.set(key);
    }

    /**
     * @return 当前方言的键
     */
    public String current() {
        String key = holder.get();
        if (key == null) {
            return defaultKey;
        }
        return key;
    }

    /**
     * 查找当前的方言
     * @return Dialect
     */
    public Dialect lookup() {
        String key = current();
        Dialect dialect = dialectMap.get(key);
        if (dialect == null) {
            throw new SearchException("No Dialect for key: " + key);
        }
        return dialect;
    }

    @Override
    public void toUpperCase(StringBuilder builder, String dbField) {
        lookup().toUpperCase(builder, dbField);
    }

    @Override
    public SqlWrapper<Object> forPaginate(String fieldSelectSql, String fromWhereSql, Paging paging) {
        return lookup().forPaginate(fieldSelectSql, fromWhereSql, paging);
    }

    @Override
    public boolean hasILike() {
        return lookup().hasILike();
    }

    public String getDefaultKey() {
        return defaultKey;
    }

    /**
     * 设置默认方言的键
     * @param defaultKey 默认方言的键
     */
    public void setDefaultKey(String defaultKey) {
        this.defaultKey = defaultKey;
    }

    /**
     * 添加方言
     * @param key 键
     * @param dialect 方言
     */
    public void put(String key, Dialect dialect) {
        dialectMap.put(key, dialect);
    }

    /**
     * 添加方法
     * @param dialectMap 方言集合
     */
    public void put(Map<String, Dialect> dialectMap) {
        this.dialectMap.putAll(dialectMap);
    }

    public Map<String, Dialect> getDialectMap() {
        return dialectMap;
    }

}
