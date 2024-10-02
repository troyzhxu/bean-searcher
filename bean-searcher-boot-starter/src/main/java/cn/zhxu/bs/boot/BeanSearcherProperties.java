package cn.zhxu.bs.boot;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.MapSearcher;
import cn.zhxu.bs.boot.prop.FieldConvertor;
import cn.zhxu.bs.boot.prop.Params;
import cn.zhxu.bs.boot.prop.Sql;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bean-searcher")
public class BeanSearcherProperties {

    /**
     * 检索参数相关配置
     */
    private final Params params = new Params();

    /**
     * SQL 相关配置
     */
    private final Sql sql = new Sql();

    /**
     * 字段转换器相关配置
     */
    private final FieldConvertor fieldConvertor = new FieldConvertor();

    /**
     * 是否使用 {@link MapSearcher } 检索器，默认为 true
     */
    private boolean useMapSearcher = true;

    /**
     * 是否使用 {@link BeanSearcher } 检索器，默认为 true
     */
    private boolean useBeanSearcher = true;


    public Params getParams() {
        return params;
    }

    public Sql getSql() {
        return sql;
    }

    public FieldConvertor getFieldConvertor() {
        return fieldConvertor;
    }

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
