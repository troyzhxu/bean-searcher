package cn.zhxu.bs.filter;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.IllegalParamException;
import cn.zhxu.bs.ParamFilter;
import cn.zhxu.bs.util.RpcNames;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 下标数组参数过滤器, 用于兼容如下形式的数组参数：
 * <pre>{@code key[0]=v1 & key[1]=v2 }</pre>
 * @author Troy.Zhou @ 2024-11-08
 * @since v4.4
 */
public class IndexArrayParamFilter implements ParamFilter {

    static final Pattern KEY_PATTERN = Pattern.compile("([a-zA-Z0-9_]+)\\[([0-9]+)]");

    private final String separator;

    public IndexArrayParamFilter() {
        this(RpcNames.DEFAULT.separator());
    }

    public IndexArrayParamFilter(String separator) {
        this.separator = Objects.requireNonNull(separator);
    }

    @Override
    public <T> Map<String, Object> doFilter(BeanMeta<T> beanMeta, Map<String, Object> paraMap) throws IllegalParamException {
        Map<String, Object> newMap = null;
        for (Map.Entry<String, Object> entry : paraMap.entrySet()) {
            String key = entry.getKey();
            Matcher matcher = KEY_PATTERN.matcher(key);
            if (!matcher.matches()) {
                continue;
            }
            if (newMap == null) {
                newMap = new HashMap<>();
            }
            String field = matcher.group(1);
            String index = matcher.group(2);
            newMap.put(field + separator + index, entry.getValue());
        }
        if (newMap != null) {
            paraMap.putAll(newMap);
        }
        return paraMap;
    }

    public String getSeparator() {
        return separator;
    }

}
