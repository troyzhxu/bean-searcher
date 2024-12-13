package cn.zhxu.bs.filter;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.FieldOpPool;
import cn.zhxu.bs.ParamFilter;
import cn.zhxu.bs.ParamNames;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 后缀运算符参数值过滤器，用于简化前端传参，例如
 * <pre>{@code age-gt=25 替代 age=25 & age-op=gt }</pre>
 * 如果与 {@link JsonArrayParamFilter } 一起使用，则本过滤器需要放在它的前面，才能支持
 * <pre>{@code age-bt=[20,30] }</pre>
 * 这样的用法
 * @author Troy.Zhou @ 2024-06-07
 * @since v4.3
 */
public class SuffixOpParamFilter implements ParamFilter {

    private final FieldOpPool fieldOpPool;
    private final String separator;
    private final String operatorKey;
    private final String ignoreCaseKey;

    private static final String defaultIgnoreCaseKey = new ParamNames<>().ic();

    public SuffixOpParamFilter() {
        this(FieldOpPool.DEFAULT, "-", "op", defaultIgnoreCaseKey);
    }

    public SuffixOpParamFilter(String separator, String operatorKey) {
        this(FieldOpPool.DEFAULT, separator, operatorKey, defaultIgnoreCaseKey);
    }

    public SuffixOpParamFilter(String separator, String operatorKey, String ignoreCaseKey) {
        this(FieldOpPool.DEFAULT, separator, operatorKey, ignoreCaseKey);
    }

    public SuffixOpParamFilter(FieldOpPool fieldOpPool, String separator, String operatorKey) {
        this(fieldOpPool, separator, operatorKey, defaultIgnoreCaseKey);
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
            int idx = key.indexOf(separator);
            if (idx < 1 || idx >= key.length() - 1) {
                continue;
            }

            String opName = null;
            String ignoreCase = null;
            String subKey = key.substring(idx + 1);
            int secondIdx = subKey.indexOf(separator);
            if (secondIdx < 1 || secondIdx >= subKey.length() - 1){
                opName = subKey;
            }else {
                opName = subKey.substring(0, secondIdx);
                ignoreCase = subKey.substring(secondIdx + 1);
            }

            if (fieldOpPool.getFieldOp(opName) == null) {
                continue;
            }
            if (newMap == null) {
                newMap = new HashMap<>();
            }
            String field = key.substring(0, idx);
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

            if (ignoreCaseKey.equals(ignoreCase)){
                newMap.put(field + separator + ignoreCase, true);
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

}
