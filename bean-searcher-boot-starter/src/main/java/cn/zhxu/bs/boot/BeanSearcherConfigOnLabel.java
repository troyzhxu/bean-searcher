package cn.zhxu.bs.boot;

import cn.zhxu.bs.ResultFilter;
import cn.zhxu.bs.label.LabelForResultFilter;
import cn.zhxu.bs.label.LabelLoader;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 标签加载器自动配置
 * @since v4.4.0
 */
@Configuration
@ConditionalOnClass(LabelForResultFilter.class)
public class BeanSearcherConfigOnLabel {

    @Bean
    @ConditionalOnMissingBean(LabelForResultFilter.class)
    public ResultFilter labelForResultFilter(ObjectProvider<List<LabelLoader<?>>> loadersProvider) {
        List<LabelLoader<?>> loaders = loadersProvider.getIfAvailable();
        if (loaders == null) {
            return new LabelForResultFilter();
        }
        return new LabelForResultFilter(loaders);
    }

}
