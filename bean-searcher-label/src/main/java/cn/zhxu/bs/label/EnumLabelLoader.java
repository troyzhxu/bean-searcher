package cn.zhxu.bs.label;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 枚举标签加载器
 * @since v4.4.0
 */
public class EnumLabelLoader implements LabelLoader<Enum<?>> {

    private final String keyPrefix;
    private final Map<Class<Enum<?>>, Function<Enum<?>, String>> labelFunMap = new ConcurrentHashMap<>();

    public EnumLabelLoader() {
        this("");
    }

    public EnumLabelLoader(String keyPrefix) {
        this.keyPrefix = Objects.requireNonNull(keyPrefix);
    }

    @Override
    public boolean supports(String key) {
        return key != null && key.startsWith(keyPrefix);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Label<Enum<?>>> load(String key, List<Enum<?>> enums) {
        return (List<Label<Enum<?>>>) (List<?>) enums.stream()
                .map(e -> {
                    Function<Enum<?>, String> fun = labelFunMap.get(e.getClass());
                    String label = fun != null ? fun.apply(e) : null;
                    return label != null ? new Label<>(e, label) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum<?>> void put(Class<T> enumType, Function<T, String> labelFun) {
        labelFunMap.put((Class<Enum<?>>) enumType, (Function<Enum<?>, String>) labelFun);
    }

    public void clear() {
        labelFunMap.clear();
    }

}
