package cn.zhxu.bs.solon.integration;


import cn.zhxu.bs.solon.BeanSearcherAutoConfiguration;
import cn.zhxu.bs.solon.BeanSearcherProperties;
import cn.zhxu.bs.solon.JsonFieldConvertorConfig;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear 2023/2/18 created
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        context.beanMake(BeanSearcherProperties.class);

        //容器加载完成后再执行，确保用户的 Bean 优先
        context.beanOnloaded(x -> {
            x.beanMake(BeanSearcherAutoConfiguration.class);
            x.beanMake(JsonFieldConvertorConfig.class);
        });
    }
}
