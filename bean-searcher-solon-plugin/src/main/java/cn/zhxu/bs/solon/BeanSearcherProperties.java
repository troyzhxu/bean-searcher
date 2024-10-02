package cn.zhxu.bs.solon;

import cn.zhxu.bs.BeanSearcher;
import cn.zhxu.bs.MapSearcher;
import cn.zhxu.bs.solon.prop.FieldConvertor;
import cn.zhxu.bs.solon.prop.Params;
import cn.zhxu.bs.solon.prop.Sql;
import org.noear.solon.annotation.Inject;

@org.noear.solon.annotation.Configuration
@Inject(value = "${bean-searcher}", required = false)
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
