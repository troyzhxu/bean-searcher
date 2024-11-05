package com.example.demo;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.ParamFilter;
import cn.zhxu.bs.util.MapUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.MultiMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于自动加载前端传来的检索参数
 * 注意：solon v2.9.0 ~ v2.9.2 不支持使用 @Bean + 匿名类 的方式注册 Bean
 * 这里改为 @Component 的方式来注册
 */
@Component
public class ReqParamFilter implements ParamFilter {

    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) {
        System.out.println("ReqParamFilter");
        Context context = Context.current();
        if (context == null) {
            return paraMap;
        }
        // 加载当前请求的所有参数
        Map<String, String[]> params = new HashMap<>();
        MultiMap<String> ctxMap = context.paramMap();
        for (String key : ctxMap.keySet()) {
            List<String> values = ctxMap.getAll(key);
            if (values != null) {
                params.put(key, values.toArray(new String[0]));
            }
        }
        return MapUtils.flatBuilder(params).putAll(paraMap).build();
    }

}
