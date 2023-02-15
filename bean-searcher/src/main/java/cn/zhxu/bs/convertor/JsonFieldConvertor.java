package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.bean.DbType;
import cn.zhxu.bs.implement.DefaultBeanReflector;
import cn.zhxu.bs.util.StringUtils;
import cn.zhxu.xjson.JsonKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [Json 字符串 to 对象] 字段转换器
 * 与 {@link DefaultBeanReflector } 配合使用
 * @author Troy.Zhou @ 2021-11-01
 * @since v4.0.0
 */
public class JsonFieldConvertor implements FieldConvertor.BFieldConvertor {

    static final Logger log = LoggerFactory.getLogger(JsonFieldConvertor.class);

    /**
     * 当遇到某些值 JSON 解析异常时，是否抛出异常
     * @since v4.0.1
     */
    private boolean failOnError = true;

    public JsonFieldConvertor() { }

    public JsonFieldConvertor(boolean failOnError) {
        this.failOnError = failOnError;
    }

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        if (valueType == String.class) {
            return meta.getType() != String.class && meta.getDbType() == DbType.JSON;
        }
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        String json = (String) value;
        if (StringUtils.isBlank(json)) {
            return null;
        }
        Class<?> type = meta.getType();
        if (failOnError) {
            return JsonKit.toBean(type, json);
        }
        try {
            return JsonKit.toBean(type, json);
        } catch (Exception e) {
            log.warn("Json parse error [{}] for {}, the value is: {}", e.getClass().getName(), type, json);
            return null;
        }
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

}
