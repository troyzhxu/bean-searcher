package cn.zhxu.bs.implement;

import java.util.Collection;
import java.util.StringJoiner;

/**
 * 拼接参数序列化器
 * @author Troy.Zhou @ 2024-06-08
 * @since v4.3
 */
public class JoinParaSerializer {

    /**
     * 序列化拼接参数
     * @param value 用户传入的拼接参数值
     * @return 序列化结果，用于拼接在 SQL 内
     */
    public String serialize(Object value) {
        if (value instanceof Collection) {
            StringJoiner joiner = new StringJoiner(",");
            ((Collection<?>) value).forEach(v -> joiner.add(itemOf(v)));
            return joiner.toString();
        } else if (value instanceof Object[]) {
            StringJoiner joiner = new StringJoiner(",");
            for (Object v : (Object[]) value) {
                joiner.add(itemOf(v));
            }
            return joiner.toString();
        } else if (value instanceof int[]) {
            StringJoiner joiner = new StringJoiner(",");
            for (int v : (int[]) value) {
                joiner.add(Integer.toString(v));
            }
            return joiner.toString();
        } else if (value instanceof long[]) {
            StringJoiner joiner = new StringJoiner(",");
            for (long v : (long[]) value) {
                joiner.add(Long.toString(v));
            }
            return joiner.toString();
        } else if (value instanceof short[]) {
            StringJoiner joiner = new StringJoiner(",");
            for (short v : (short[]) value) {
                joiner.add(Short.toString(v));
            }
            return joiner.toString();
        } else if (value instanceof byte[]) {
            StringJoiner joiner = new StringJoiner(",");
            for (byte v : (byte[]) value) {
                joiner.add(Byte.toString(v));
            }
            return joiner.toString();
        } else if (value instanceof boolean[]) {
            StringJoiner joiner = new StringJoiner(",");
            for (boolean v : (boolean[]) value) {
                joiner.add(Boolean.toString(v));
            }
            return joiner.toString();
        }
        // 非集合或数组
        return value != null ? value.toString() : "";
    }

    protected String itemOf(Object value) {
        if (value instanceof String) {
            return "'" + value + "'";
        }
        return String.valueOf(value);
    }

}
