package com.ejlchina.searcher.filter;

import com.ejlchina.searcher.BeanMeta;
import com.ejlchina.searcher.ParamFilter;

import java.util.Map;

/**
 * 参数大小限制过滤器
 * 风险控制，用于避免前端恶意传参生成过于复杂的 SQL
 * @author Troy.Zhou @ 2017-07-07
 * @since v3.8.1
 */
public class SizeLimitParamFilter implements ParamFilter {

    private int maxAllowedSize = 150;

    public SizeLimitParamFilter() { }

    public SizeLimitParamFilter(int maxAllowedSize) {
        this.maxAllowedSize = maxAllowedSize;
    }

    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
        int size = paraMap.size();
        if (size > maxAllowedSize) {
            throw new IllegalArgumentException("paraMap's size is too large: " + size + ", the max allowed size is: " + maxAllowedSize);
        }
        return paraMap;
    }

    public int getMaxAllowedSize() {
        return maxAllowedSize;
    }

    public void setMaxAllowedSize(int maxAllowedSize) {
        this.maxAllowedSize = maxAllowedSize;
    }

}

