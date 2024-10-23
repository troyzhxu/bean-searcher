package com.example.config;


import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.ParamFilter;
import cn.zhxu.bs.util.MapBuilder;
import cn.zhxu.bs.util.MapUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 自动加载当前请求参数，参见：
 * https://bs.zhxu.cn/guide/usage/others.html
 */
@Component
public class AutoLoadParamFilter implements ParamFilter {

    // 定义一个常量，作为一个开关，当启用时，则取消自动加载功能
    public static final String IGNORE_REQUEST_PARAMS = "IGNORE_REQUEST_PARAMS";

    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
        MapBuilder builder;
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes &&
                !paraMap.containsKey(IGNORE_REQUEST_PARAMS)) {
            // 在一个 Web 请求上下文中，并且没有开启 IGNORE_REQUEST_PARAMS，则取出前端传来的所有参数
            HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
            builder = MapUtils.flatBuilder(request.getParameterMap());
        } else {
            builder = MapUtils.builder();
        }
        // 自定义查询参数，优先级最高，可以覆盖上面的参数
        builder.putAll(paraMap);
        return builder.build();
    }

}
