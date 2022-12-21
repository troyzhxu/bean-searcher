package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.bean.DbType;
import cn.zhxu.bs.implement.DefaultBeanReflector;
import cn.zhxu.xjson.JsonKit;

/**
 * [字符串 to 数字] 字段转换器
 * 与 {@link DefaultBeanReflector } 配合使用
 * @author Troy.Zhou @ 2021-11-01
 * @since v4.0.0
 */
public class JsonFieldConvertor implements FieldConvertor.BFieldConvertor {

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        if (valueType == String.class) {
            return meta.getType() != String.class && meta.getDbType() == DbType.JSON;
        }
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        return JsonKit.toBean(meta.getType(), (String) value);
    }

}
