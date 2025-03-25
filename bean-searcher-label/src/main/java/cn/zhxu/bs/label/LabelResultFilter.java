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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 标签字段结果过滤器
 * @since v4.4.0
 */
public class LabelResultFilter implements ResultFilter {

    private final List<LabelLoader<?>> labelLoaders;
    private final Map<Class<?>, Map<LabelKey, List<LabelField>>> cache = new ConcurrentHashMap<>();

    public LabelResultFilter() {
        this.labelLoaders = new ArrayList<>();
    }

    public LabelResultFilter(List<LabelLoader<?>> labelLoaders) {
        this.labelLoaders = new ArrayList<>(labelLoaders);
    }

    @Override
    public <T> SearchResult<T> doBeanFilter(SearchResult<T> result, BeanMeta<T> beanMeta, Map<String, Object> paraMap, FetchType fetchType) {
        return processResult(result, beanMeta);
    }

    @Override
    public <T> SearchResult<Map<String, Object>> doMapFilter(SearchResult<Map<String, Object>> result, BeanMeta<T> beanMeta, Map<String, Object> paraMap, FetchType fetchType) {
        return processResult(result, beanMeta);
    }

    public <T> SearchResult<T> processResult(SearchResult<T> result, BeanMeta<?> beanMeta) {
        List<?> dataList = result.getDataList();
        if (dataList != null && !dataList.isEmpty()) {
            processDataList(beanMeta.getBeanClass(), dataList);
        }
        return result;
    }

    public void processDataList(Class<?> beanClass, List<?> dataList) {
        loadLabelFieldMap(beanClass).forEach((key, fields) -> {
            List<Object> ids = dataList.stream()
                    .flatMap(data -> fields.stream().map(field -> field.id(data)))
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            if (ids.isEmpty()) {
                return;
            }
            List<Label<?>> labels = loadLabels(beanClass, key, ids);
            if (labels.isEmpty()) {
                return;
            }
            for (LabelField field : fields) {
                fillLabels(field, dataList, labels);
            }
        });
    }

    protected void fillLabels(LabelField field, List<?> dataList, List<Label<?>> labels) {
       for (Object data : dataList) {
           Object id = field.id(data);
           for (Label<?> label : labels) {
               if (Objects.equals(label.getId(), id)) {
                   field.setLabel(data, label.getLabel());
                   break;
               }
           }
       }
    }

    @SuppressWarnings("unchecked")
    protected List<Label<?>> loadLabels(Class<?> beanClass, LabelKey key, List<Object> ids) {
        for (LabelLoader<?> labelLoader : labelLoaders) {
            if (key.supports(labelLoader)) {
                LabelLoader<Object> loader = (LabelLoader<Object>) labelLoader;
                List<Label<Object>> labels = loader.load(key.getKey(), ids);
                return (List<Label<?>>) (List<?>) labels;
            }
        }
        throw new SearchException("Can not load labels for " + key + " on " + beanClass + ", please add a LabelLoader for it.");
    }

    protected Map<LabelKey, List<LabelField>> loadLabelFieldMap(Class<?> beanClass) {
        Map<LabelKey, List<LabelField>> fieldsMap = cache.get(beanClass);
        if (fieldsMap != null) {
            return fieldsMap;
        }
        synchronized (cache) {
            Map<LabelKey, List<LabelField>> map = cache.get(beanClass);
            if (map == null) {
                map = resolveLabelFieldKeys(beanClass).stream()
                        .collect(Collectors.groupingBy(
                                LabelField.KEY::key,
                                Collectors.mapping(LabelField.KEY::field, Collectors.toList())
                        ));
                cache.put(beanClass, map);
            }
            return map;
        }
    }

    protected List<LabelField.KEY> resolveLabelFieldKeys(Class<?> beanClass) {
        List<LabelField.KEY> fieldList = new ArrayList<>();
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
                        Field idField = requireField(beanClass, labelFor.value());
                        idField.setAccessible(true);
                        field.setAccessible(true);
                        String key = labelFor.key();
                        fieldList.add(new LabelField.KEY(
                                field, idField,
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

    protected Field requireField(Class<?> beanClass, String fieldName) throws NoSuchFieldException {
        try {
            return beanClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = beanClass.getSuperclass();
            if (superClass == null || superClass == Object.class) {
                throw e;
            }
            return requireField(superClass, fieldName);
        }
    }

    public void addLabelLoader(LabelLoader<?> labelLoader) {
        labelLoaders.add(labelLoader);
    }

    public void removeLabelLoader(LabelLoader<?> labelLoader) {
        labelLoaders.remove(labelLoader);
    }

    public void clearLabelLoaders() {
        labelLoaders.clear();
    }

    public void clearCache() {
        cache.clear();
    }

}
