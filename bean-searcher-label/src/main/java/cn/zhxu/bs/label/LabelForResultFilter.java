package cn.zhxu.bs.label;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.ResultFilter;
import cn.zhxu.bs.SearchException;
import cn.zhxu.bs.SearchResult;
import cn.zhxu.bs.param.FetchType;
import cn.zhxu.bs.util.AnnoUtils;
import cn.zhxu.bs.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 标签字段结果过滤器
 * @since v4.4.0
 */
public class LabelForResultFilter implements ResultFilter {

    private final List<LabelLoader<?>> labelLoaders;

    public LabelForResultFilter() {
        this.labelLoaders = new ArrayList<>();
    }

    public LabelForResultFilter(List<LabelLoader<?>> labelLoaders) {
        this.labelLoaders = Objects.requireNonNull(labelLoaders);
    }

    @Override
    public <T> SearchResult<T> doBeanFilter(SearchResult<T> result, BeanMeta<T> beanMeta, Map<String, Object> paraMap, FetchType fetchType) {
        List<T> dataList = result.getDataList();
        if (dataList == null || dataList.isEmpty()) {
            return result;
        }
        // TODO:

        return result;
    }

    @Override
    public <T> SearchResult<Map<String, Object>> doMapFilter(SearchResult<Map<String, Object>> result, BeanMeta<T> beanMeta, Map<String, Object> paraMap, FetchType fetchType) {
        List<Map<String, Object>> dataList = result.getDataList();
        if (dataList == null || dataList.isEmpty()) {
            return result;
        }
        // TODO:

        return result;
    }

    protected List<LabelField> getLabelForFields(BeanMeta<?> beanMeta) {
        Class<?> beanClass = beanMeta.getBeanClass();
        List<LabelField> fieldList = new ArrayList<>();
        Set<String> fieldNames = new HashSet<>();
        while (beanClass != Object.class) {
            for (Field field : beanClass.getDeclaredFields()) {
                String name = field.getName();
                if (field.isSynthetic() || Modifier.isStatic(field.getModifiers())
                        || fieldNames.contains(name)) {
                    continue;
                }
                LabelFor labelFor = AnnoUtils.getAnnotation(field, LabelFor.class);
                if (labelFor != null) {
                    try {
                        Field forField = beanClass.getField(labelFor.value());
                        String key = labelFor.key();
                        fieldList.add(new LabelField(
                                field, forField,
                                StringUtils.isBlank(key) ? field.getName() : key
                        ));
                    } catch (NoSuchFieldException e) {
                        throw new SearchException("The field [" + labelFor.value() + "] is not found in [" + beanClass.getName() + "]", e);
                    }
                }
                fieldNames.add(name);
            }
            beanClass = beanClass.getSuperclass();
        }
        return fieldList;
    }

    public void addLabelLoader(LabelLoader<?> labelLoader) {
        labelLoaders.add(labelLoader);
    }

    public void clearLabelLoaders() {
        labelLoaders.clear();
    }

}
