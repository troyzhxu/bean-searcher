package cn.zhxu.bs.solon;

import cn.zhxu.bs.FieldOpPool;
import cn.zhxu.bs.filter.*;
import cn.zhxu.bs.solon.prop.BeanSearcherParams;
import cn.zhxu.bs.solon.prop.BeanSearcherProperties;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

@Configuration
public class BeanSearcherParamFilters {

    //放到这儿，减少注入处理代码
    @Inject
    BeanSearcherProperties config;

    @Bean(index = -100)
    @Condition(onMissingBean = SizeLimitParamFilter.class,
            onProperty = "${bean-searcher.params.filter.use-size-limit:true}=true")
    public SizeLimitParamFilter sizeLimitParamFilter() {
        return new SizeLimitParamFilter(config.getParams().getFilter().getMaxParaMapSize());
    }

    @Bean(index = 100)
    @Condition(onMissingBean = ArrayValueParamFilter.class,
            onProperty = "${bean-searcher.params.filter.use-array-value:true}=true")
    public ArrayValueParamFilter arrayValueParamFilter() {
        return new ArrayValueParamFilter(config.getParams().getSeparator());
    }

    @Bean(index = 200)
    @Condition(onMissingBean = SuffixOpParamFilter.class,
            onProperty = "${bean-searcher.params.filter.use-suffix-op}=true")
    public SuffixOpParamFilter suffixOpParamFilter(FieldOpPool fieldOpPool) {
        BeanSearcherParams cfg = config.getParams();
        return new SuffixOpParamFilter(fieldOpPool, cfg.getSeparator(), cfg.getOperatorKey(), cfg.getIgnoreCaseKey());
    }

    @Bean(index = 300)
    @Condition(onMissingBean = JsonArrayParamFilter.class, onClass = cn.zhxu.xjson.JsonKit.class,
            onProperty = "${bean-searcher.params.filter.use-json-array}=true")
    public JsonArrayParamFilter jsonArrayParamFilter() {
        return new JsonArrayParamFilter(config.getParams().getSeparator());
    }

    @Bean(index = 400)
    @Condition(onMissingBean = IndexArrayParamFilter.class,
            onProperty = "${bean-searcher.params.filter.use-index-array}=true")
    public IndexArrayParamFilter indexArrayParamFilter(BeanSearcherParams config) {
        return new IndexArrayParamFilter(config.getSeparator());
    }

}
