package cn.zhxu.bs.solon;

import cn.zhxu.bs.solon.prop.BeanSearcherProperties;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 2.1
 */
public class SolonPlugin implements Plugin {

    @Override
    public void start(AppContext context) {
        //先构建配置
        context.beanMake(BeanSearcherProperties.class);

        context.beanMake(BeanSearcherParamFilters.class);
        context.beanMake(BeanSearcherConvertors.class);

        //再构建托管对象
        context.beanMake(BeanSearcherConfiguration.class);

    }

}
