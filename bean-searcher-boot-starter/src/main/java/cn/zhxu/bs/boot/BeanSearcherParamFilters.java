package cn.zhxu.bs.boot;

import cn.zhxu.bs.FieldOpPool;
import cn.zhxu.bs.boot.prop.BeanSearcherParams;
import cn.zhxu.bs.filter.ArrayValueParamFilter;
import cn.zhxu.bs.filter.IndexArrayParamFilter;
import cn.zhxu.bs.filter.SizeLimitParamFilter;
import cn.zhxu.bs.filter.SuffixOpParamFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class BeanSearcherParamFilters {

    @Bean
    @Order(-100)
    @ConditionalOnMissingBean(SizeLimitParamFilter.class)
    @ConditionalOnProperty(name = "bean-searcher.params.filter.use-size-limit", havingValue = "true", matchIfMissing = true)
    public SizeLimitParamFilter sizeLimitParamFilter(BeanSearcherParams config) {
        return new SizeLimitParamFilter(config.getFilter().getMaxParaMapSize());
    }

    @Bean
    @Order(100)
    @ConditionalOnMissingBean(ArrayValueParamFilter.class)
    @ConditionalOnProperty(name = "bean-searcher.params.filter.use-array-value", havingValue = "true", matchIfMissing = true)
    public ArrayValueParamFilter arrayValueParamFilter(BeanSearcherParams config) {
        return new ArrayValueParamFilter(config.getSeparator());
    }

    @Bean
    @Order(200)
    @ConditionalOnMissingBean(SuffixOpParamFilter.class)
    @ConditionalOnProperty(name = "bean-searcher.params.filter.use-suffix-op", havingValue = "true")
    public SuffixOpParamFilter suffixOpParamFilter(FieldOpPool fieldOpPool, BeanSearcherParams cfg) {
        return new SuffixOpParamFilter(fieldOpPool, cfg.getSeparator(), cfg.getOperatorKey(), cfg.getIgnoreCaseKey());
    }

    @Bean
    @Order(400)
    @ConditionalOnMissingBean(IndexArrayParamFilter.class)
    @ConditionalOnProperty(name = "bean-searcher.params.filter.use-index-array", havingValue = "true")
    public IndexArrayParamFilter indexArrayParamFilter(BeanSearcherParams config) {
        return new IndexArrayParamFilter(config.getSeparator());
    }

}
