package com.example.config;


import com.ejlchina.json.JSONKit;
import com.ejlchina.searcher.BeanMeta;
import com.ejlchina.searcher.ParamFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * 为了简化多值参数传递
 * 参考：https://github.com/ejlchina/bean-searcher/issues/10
 */
public class JsonArrParamFilter implements ParamFilter {

    private final String OP_SUFFIX = "-op";

    private final String[] OPS = new String[] { "mv", "bt" };

    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
        Map<String, Object> newParaMap = new HashMap<>();
        paraMap.forEach((key, value) -> {
            if (key == null) {
                return;
            }
            boolean isOpKey = key.endsWith(OP_SUFFIX);
            String opKey = isOpKey ? key : key + OP_SUFFIX;
            Object opVal = paraMap.get(opKey);
            if (shouldSkip(opVal)) {
                newParaMap.put(key, value);
                return;
            }
            if (newParaMap.containsKey(key)) {
                return;
            }
            String valKey = key;
            Object valVal = value;
            if (isOpKey) {
                valKey = key.substring(0, key.length() - OP_SUFFIX.length());
                valVal = paraMap.get(valKey);
            }
            if (likelyJsonArr(valVal)) {
                try {
                    String vKey = valKey;
                    JSONKit.toArray((String) valVal).forEach(
                            (index, data) -> newParaMap.put(vKey + "-" + index, data.toString())
                    );
                    newParaMap.put(opKey, opVal);
                    return;
                } catch (Exception ignore) {}
            }
            newParaMap.put(key, value);
        });
        return newParaMap;
    }

    private boolean shouldSkip(Object op) {
        for (String target : OPS) {
            if (target.equals(op)) {
                return false;
            }
        }
        return true;
    }

    private boolean likelyJsonArr(Object value) {
        if (value instanceof String) {
            String str = ((String) value).trim();
            return str.startsWith("[") && str.endsWith("]");
        }
        return false;
    }

}
