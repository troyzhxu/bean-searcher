package cn.zhxu.bs.filter;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.ParamFilter;
import cn.zhxu.data.Array;
import cn.zhxu.xjson.JsonKit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * JSON 数组参数值过滤器，用于简化前端传参，例如 age=[20,30] 替代 age-0=20 & age-1=30
 * @author Troy.Zhou @ 2024-06-07
 * @since v4.3
 */
public class JsonArrayParamFilter implements ParamFilter {

    private String separator = "-";

    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
        Map<String, Object> newMap = null;
        for (Map.Entry<String, Object> entry : paraMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!likelyJsonArr(value)) {
                continue;
            }
            if (key == null || key.contains(separator)) {
                continue;
            }
            Array array;
            try {
                array = JsonKit.toArray((String) value);
            } catch (Exception ignore) {
                continue;
            }
            if (array.isEmpty()) {
                continue;
            }
            if (newMap == null) {
                newMap = new HashMap<>();
            }
            for (int i = 0; i < array.size(); i++) {
                newMap.put(key + separator + i, array.getString(i));
            }
        }
        if (newMap != null) {
            paraMap.putAll(newMap);
        }
        return paraMap;
    }

    protected boolean likelyJsonArr(Object value) {
        if (value instanceof String) {
            String s = (String) value;
            return s.startsWith("[") && s.endsWith("]");
        }
        return false;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = Objects.requireNonNull(separator);
    }

}
