package cn.zhxu.bs.solon;

import cn.zhxu.bs.convertor.JsonFieldConvertor;
import cn.zhxu.xjson.JsonKit;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AopContext;

/**
 * 注解 @ConditionalOnClass 不能与 @Bean 放在一起，否则当没有条件中的 Class 时，会出现错误：
 * java.lang.ArrayStoreException: sun.reflect.annotation.TypeNotPresentExceptionProxy
 */
@Condition(hasClass = JsonKit.class, hasProperty = "${bean-searcher.field-convertor.use-json}=true")
@Configuration
public class JsonFieldConvertorConfig {
    @Inject
    AopContext context;

    @Bean
    public JsonFieldConvertor jsonFieldConvertor(BeanSearcherProperties config) {
        if(context.hasWrap(JsonFieldConvertor.class) == false){
            return null;
        }

        BeanSearcherProperties.FieldConvertor conf = config.getFieldConvertor();
        return new JsonFieldConvertor(conf.isJsonFailOnError());
    }
}