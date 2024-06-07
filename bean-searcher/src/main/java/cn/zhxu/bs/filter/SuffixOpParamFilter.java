package cn.zhxu.bs.filter;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.FieldOpPool;
import cn.zhxu.bs.ParamFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 后缀运算符参数值过滤器，用于简化前端传参，例如 age-gt=25 替代 age=25 & age-op=gt
 * @author Troy.Zhou @ 2024-06-07
 * @since v4.3
 */
public class SuffixOpParamFilter implements ParamFilter {

    private FieldOpPool fieldOpPool = FieldOpPool.DEFAULT;
    private String separator = "-";
    private String operatorSuffix = "op";

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
            String opName = key.substring(idx + 1);
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
            newMap.put(field + separator + operatorSuffix, opName);
        }
        if (newMap != null) {
            paraMap.putAll(newMap);
        }
        return paraMap;
    }

    public FieldOpPool getFieldOpPool() {
        return fieldOpPool;
    }

    public void setFieldOpPool(FieldOpPool fieldOpPool) {
        this.fieldOpPool = Objects.requireNonNull(fieldOpPool);
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = Objects.requireNonNull(separator);
    }

    public String getOperatorSuffix() {
        return operatorSuffix;
    }

    public void setOperatorSuffix(String operatorSuffix) {
        this.operatorSuffix = Objects.requireNonNull(operatorSuffix);
    }

}
