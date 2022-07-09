package com.ejlchina.searcher.filter;

import com.ejlchina.searcher.BeanMeta;
import com.ejlchina.searcher.IllegalParamException;
import com.ejlchina.searcher.ParamFilter;

import java.util.Map;

/**
 * 参数大小限制过滤器
 * 风险控制，用于避免前端恶意传参生成过于复杂的 SQL
 * @author Troy.Zhou @ 2017-07-07
 * @since v3.8.1
 */
public class SizeLimitParamFilter implements ParamFilter {

    private int maxParaMapSize = 150;

    public SizeLimitParamFilter() { }

    public SizeLimitParamFilter(int maxParaMapSize) {
        this.maxParaMapSize = maxParaMapSize;
    }

    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap)
            throws IllegalParamException {
        int size = paraMap.size();
        if (size > maxParaMapSize) {
            throw new IllegalParamException("A too large paraMap sized of " + size + ", max allowed " + maxParaMapSize);
        }
        return paraMap;
    }

    public int getMaxParaMapSize() {
        return maxParaMapSize;
    }

    public void setMaxParaMapSize(int maxParaMapSize) {
        this.maxParaMapSize = maxParaMapSize;
    }

}

