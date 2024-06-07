package cn.zhxu.bs.filter;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.IllegalParamException;
import cn.zhxu.bs.ParamFilter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 用于转换拼接参数的集合参数值，一般与拼接参数配合使用，启用时，可让框架支持以下用法：
 * <pre> {@code
 * @SearchBean(where="id in (:idList:)")
 * }</pre>
 * 然后检索参数中可以为拼接参数 idList 直接添加集合参数值：
 * <pre> {@code
 * Map<String, Object> params = MapUtils.builder()
 *     .put("idList", Arrays.asList(1, 2, 3));
 *     .build();
 * }</pre>
 * @author Troy.Zhou @ 2024-06-06
 * @since v4.3
 */
public class JoinParaValueParamFilter implements ParamFilter {

    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap)
            throws IllegalParamException {
        List<String> joinParaNames = beanMeta.getJoinParaNames();
        paraMap.forEach((key, value) -> {
            if (value instanceof Collection && joinParaNames.contains(key)) {
                StringJoiner joiner = new StringJoiner(",");
                ((Collection<?>) value).forEach(v -> joiner.add(valueOf(v)));
                paraMap.put(key, joiner.toString());
            }
        });
        return paraMap;
    }

    protected String valueOf(Object value) {
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof String) {
            return "'" + value + "'";
        }
        if (value == null) {
            return "null";
        }
        throw new IllegalParamException("非法的集合元素类型：" + value.getClass());
    }

}
