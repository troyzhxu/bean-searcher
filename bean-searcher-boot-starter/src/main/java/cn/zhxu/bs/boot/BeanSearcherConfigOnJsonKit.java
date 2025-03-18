package cn.zhxu.bs.boot;

import cn.zhxu.bs.boot.prop.BeanSearcherFieldConvertor;
import cn.zhxu.bs.boot.prop.BeanSearcherParams;
import cn.zhxu.bs.convertor.JsonFieldConvertor;
import cn.zhxu.bs.filter.JsonArrayParamFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 注解 @ConditionalOnClass 不能与 @Bean 放在一起，否则当没有条件中的 Class 时，会出现错误：
 * java.lang.ArrayStoreException: sun.reflect.annotation.TypeNotPresentExceptionProxy
 */
@Configuration
@ConditionalOnClass(cn.zhxu.xjson.JsonKit.class)
public class BeanSearcherConfigOnJsonKit {

    @Bean
    @ConditionalOnProperty(name = "bean-searcher.field-convertor.use-json", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(JsonFieldConvertor.class)
    public JsonFieldConvertor jsonFieldConvertor(BeanSearcherFieldConvertor config) {
        return new JsonFieldConvertor(config.isJsonFailOnError());
    }

    @Bean
    @Order(300)
    @ConditionalOnMissingBean(JsonArrayParamFilter.class)
    @ConditionalOnProperty(name = "bean-searcher.params.filter.use-json-array", havingValue = "true")
    public JsonArrayParamFilter jsonArrayParamFilter(BeanSearcherParams config) {
        return new JsonArrayParamFilter(config.getSeparator());
    }

}
