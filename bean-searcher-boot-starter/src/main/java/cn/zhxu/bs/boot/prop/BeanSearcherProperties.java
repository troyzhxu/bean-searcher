package cn.zhxu.bs.boot.prop;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.MapSearcher;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bean-searcher")
public class BeanSearcherProperties {

    /**
     * 是否使用 {@link MapSearcher } 检索器，默认为 true
     */
    private boolean useMapSearcher = true;

    /**
     * 是否使用 {@link BeanSearcher } 检索器，默认为 true
     */
    private boolean useBeanSearcher = true;

    public boolean isUseMapSearcher() {
        return useMapSearcher;
    }

    public void setUseMapSearcher(boolean useMapSearcher) {
        this.useMapSearcher = useMapSearcher;
    }

    public boolean isUseBeanSearcher() {
        return useBeanSearcher;
    }

    public void setUseBeanSearcher(boolean useBeanSearcher) {
        this.useBeanSearcher = useBeanSearcher;
    }

}
