package cn.zhxu.bs.label;

import java.util.List;

/**
 * 标签加载器
 * @since v4.4.0
 */
public interface LabelLoader<ID> {

    /**
     * @param key 标签KEY
     * @return 是否支持
     */
    boolean supports(String key);

    /**
     * 加载标签
     * @param key 标签KEY
     * @param ids 标签ID列表
     * @return 标签列表
     */
    List<String> load(String key, List<ID> ids);

}
