package com.example.config;

import com.ejlchina.searcher.ParamFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanSearcherConfig {

    /**
     * 为了简化多值参数传递
     * 参考：https://github.com/troyzhxu/bean-searcher/issues/10
     * @return 参数过滤器
     */
    @Bean
    public ParamFilter myParamFilter() {
        return new JsonArrParamFilter();
    }

}
