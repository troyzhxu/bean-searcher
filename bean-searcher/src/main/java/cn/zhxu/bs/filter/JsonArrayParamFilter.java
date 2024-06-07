package cn.zhxu.bs.filter;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.ParamFilter;
import cn.zhxu.data.Array;
import cn.zhxu.xjson.JsonKit;

import java.util.Map;

/**
 * JSON 数组参数值过滤器，用于简化前端传参，例如 age=[20,30] 替代 age-0=20&age-1=30
 * @author Troy.Zhou @ 2024-06-06
 * @since v4.3
 */
public class JsonArrayParamFilter implements ParamFilter {

    private String separator = "-";

    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
        paraMap.forEach((key, value) -> {
            if (!likelyJsonArr(value)) {
                return;
            }
            if (key == null || key.contains(separator)) {
                return;
            }
            Array array;
            try {
                array = JsonKit.toArray((String) value);
            } catch (Exception ignore) {
                return;
            }
            array.forEach((idx, data) -> paraMap.put(key + separator + idx, data.toString()));
        });
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
        this.separator = separator;
    }

}
