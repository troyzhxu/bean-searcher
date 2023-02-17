package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.ParamResolver;
import cn.zhxu.bs.bean.DbType;
import cn.zhxu.bs.util.StringUtils;

/**
 * [String | Number to Boolean] 参数值转换器
 *
 * @author Troy.Zhou @ 2022-06-14
 * @since v3.8.0
 */
public class BoolParamConvertor implements ParamResolver.Convertor {

    private String[] falseValues = new String[] { "0", "OFF", "FALSE", "N", "NO", "F" };

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        return meta.getDbType() == DbType.BOOL && (
                String.class == valueType || Number.class.isAssignableFrom(valueType) || Boolean.class == valueType
        );
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        if (value instanceof Boolean) {
            return value;
        }
        if (value instanceof String) {
            String s = (String) value;
            if (StringUtils.isBlank(s)) {
                return null;
            }
            for (String f : falseValues) {
                if (s.equalsIgnoreCase(f)) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }
        return null;
    }

    public String[] getFalseValues() {
        return falseValues;
    }

    public void setFalseValues(String[] falseValues) {
        this.falseValues = falseValues;
    }

}
