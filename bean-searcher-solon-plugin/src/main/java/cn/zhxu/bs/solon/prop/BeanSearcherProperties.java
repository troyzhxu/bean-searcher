package cn.zhxu.bs.solon.prop;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.MapSearcher;
import org.noear.solon.annotation.Inject;

@org.noear.solon.annotation.Configuration
@Inject(value = "${bean-searcher}", required = false)
public class BeanSearcherProperties {

    /**
     * 检索参数相关配置
     */
    private final BeanSearcherParams params = new BeanSearcherParams();

    /**
     * SQL 相关配置
     */
    private final BeanSearcherSql sql = new BeanSearcherSql();

    /**
     * 字段转换器相关配置
     */
    private final BeanSearcherFieldConvertor fieldConvertor = new BeanSearcherFieldConvertor();

    /**
     * 是否使用 {@link MapSearcher } 检索器，默认为 true
     */
    private boolean useMapSearcher = true;

    /**
     * 是否使用 {@link BeanSearcher } 检索器，默认为 true
     */
    private boolean useBeanSearcher = true;


    public BeanSearcherParams getParams() {
        return params;
    }

    public BeanSearcherSql getSql() {
        return sql;
    }

    public BeanSearcherFieldConvertor getFieldConvertor() {
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
