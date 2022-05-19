package com.example;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.boot.BeanSearcherProperties;
import com.ejlchina.searcher.group.GroupResolver;
import com.ejlchina.searcher.implement.DefaultParamResolver;
import com.ejlchina.searcher.param.FieldParam;
import com.ejlchina.searcher.util.MapWrapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Set;


@SpringBootApplication
public class Application implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    private Object convertValue(Class<?> fieldType, Object value) {
        if (value instanceof String) {
            String str = (String) value;
            if (fieldType == int.class || fieldType == Integer.class) {
                // 将字符串的参数值转换成 Integer
                return Integer.valueOf(str);
            }
            if (fieldType == long.class || fieldType == Long.class) {
                // 将字符串的参数值转换成 Long
                return Long.valueOf(str);
            }
            // 等等 其它类型转换，按需添加即可

        }
        return value;
    }

    @Bean
    public ParamResolver myParamResolver(PageExtractor pageExtractor,
                                         GroupResolver groupResolver,
                                         ObjectProvider<ParamFilter[]> paramFilters,
                                         FieldOpPool fieldOpPool,
                                         BeanSearcherProperties config) {
        DefaultParamResolver paramResolver = new DefaultParamResolver() {
            @Override
            protected FieldParam toFieldParam(FieldMeta meta, Set<Integer> indices, MapWrapper paraMap) {
                FieldParam param = super.toFieldParam(meta, indices, paraMap);
                if (param == null) {
                    return null;
                }
                List<FieldParam.Value> vList = param.getValueList();
                if (vList.isEmpty()) {
                    return param;
                }
                Class<?> fieldType = meta.getType();
                Object[] values = param.getValues();
                vList.clear();
                for (int index = 0; index < values.length; index++) {
                    vList.add(new FieldParam.Value(convertValue(fieldType, values[index]), index));
                }
                return param;
            }
        };
        paramResolver.setPageExtractor(pageExtractor);
        paramResolver.setFieldOpPool(fieldOpPool);
        paramFilters.ifAvailable(paramResolver::setParamFilters);
        BeanSearcherProperties.Params conf = config.getParams();
        paramResolver.setOperatorSuffix(conf.getOperatorKey());
        paramResolver.setIgnoreCaseSuffix(conf.getIgnoreCaseKey());
        paramResolver.setOrderName(conf.getOrder());
        paramResolver.setSortName(conf.getSort());
        paramResolver.setOrderByName(conf.getOrderBy());
        paramResolver.setSeparator(conf.getSeparator());
        paramResolver.setOnlySelectName(conf.getOnlySelect());
        paramResolver.setSelectExcludeName(conf.getSelectExclude());
        BeanSearcherProperties.Params.Group group = conf.getGroup();
        paramResolver.setGexprName(group.getExprName());
        paramResolver.setGroupSeparator(group.getSeparator());
        paramResolver.setGroupResolver(groupResolver);
        return paramResolver;
    }


}
