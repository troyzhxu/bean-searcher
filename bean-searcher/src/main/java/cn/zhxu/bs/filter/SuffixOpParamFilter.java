package cn.zhxu.bs.filter;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.FieldOpPool;
import cn.zhxu.bs.ParamFilter;
import cn.zhxu.bs.util.RpcNames;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 后缀运算符参数值过滤器，用于简化前端传参，例如
 * <pre>{@code age-gt=25 替代 age=25 & age-op=gt }</pre>
 * 如果与 {@link JsonArrayParamFilter } 一起使用，则本过滤器需要放在它的前面，才能支持
 * <pre>{@code age-bt=[20,30] }</pre>
 * v4.3.6 开始支持 <pre>{@code name-ct-ic=xxx }</pre> 的用法
 * 这样的用法
 * @author Troy.Zhou @ 2024-06-07
 * @since v4.3
 */
public class SuffixOpParamFilter implements ParamFilter {

    private final FieldOpPool fieldOpPool;
    private final String separator;
    private final String operatorKey;
    private final String ignoreCaseKey;

    public SuffixOpParamFilter() {
        this(FieldOpPool.DEFAULT, RpcNames.DEFAULT.separator(), RpcNames.DEFAULT.op(), RpcNames.DEFAULT.ic());
    }

    public SuffixOpParamFilter(String separator, String operatorKey, String ignoreCaseKey) {
        this(FieldOpPool.DEFAULT, separator, operatorKey, ignoreCaseKey);
    }

    public SuffixOpParamFilter(FieldOpPool fieldOpPool, String separator, String operatorKey, String ignoreCaseKey) {
        this.fieldOpPool = Objects.requireNonNull(fieldOpPool);
        this.separator = Objects.requireNonNull(separator);
        this.operatorKey = Objects.requireNonNull(operatorKey);
        this.ignoreCaseKey = Objects.requireNonNull(ignoreCaseKey);
    }

    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
        Map<String, Object> newMap = null;
        for (Map.Entry<String, Object> entry : paraMap.entrySet()) {
            String key = entry.getKey();
            if (key == null) {
                continue;
            }
            int idx1 = key.indexOf(separator);
            if (idx1 < 1 || idx1 >= key.length() - 1) {
                continue;
            }
            int idx2 = key.indexOf(separator, idx1 + 1);
            // v4.3.6 开始支持形如 name-ct-ic 的参数
            String opName = idx2 < idx1
                    ? key.substring(idx1 + 1)
                    : key.substring(idx1 + 1, idx2);
            if (fieldOpPool.getFieldOp(opName) == null) {
                continue;
            }
            if (newMap == null) {
                newMap = new HashMap<>();
            }
            String field = key.substring(0, idx1);
            Object value = entry.getValue();
            if (value instanceof String[]) {
                String[] vs = (String[]) value;
                for (int i = 0; i < vs.length; i++) {
                    newMap.put(field + separator + i, vs[i]);
                }
            } else {
                newMap.put(field, value);
            }
            newMap.put(field + separator + operatorKey, opName);
            // v4.3.6 开始支持形如 name-ct-ic 的参数
            if (
                idx2 > 0 && idx2 < key.length() - 1
                && key.regionMatches(
                        idx2 + 1,
                        ignoreCaseKey,
                        0,
                        ignoreCaseKey.length()
                )
            ) {
                newMap.put(field + separator + ignoreCaseKey, true);
            }
        }
        if (newMap != null) {
            paraMap.putAll(newMap);
        }
        return paraMap;
    }

    public FieldOpPool getFieldOpPool() {
        return fieldOpPool;
    }

    public String getSeparator() {
        return separator;
    }

    public String getOperatorKey() {
        return operatorKey;
    }

    public String getIgnoreCaseKey() {
        return ignoreCaseKey;
    }

}
