package cn.zhxu.bs.label;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public class LabelKey {

    private final String key;
    private final Class<?> idType;

    public LabelKey(String key, Class<?> idType) {
        this.key = key;
        this.idType = idType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabelKey labelKey = (LabelKey) o;
        return Objects.equals(key, labelKey.key) && Objects.equals(idType, labelKey.idType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, idType);
    }

    public String getKey() {
        return key;
    }

    public boolean supports(LabelLoader<?> labelLoader) {
        return labelLoader.supports(key) && match(labelLoader.getClass());
    }

    private boolean match(Class<?> loaderClass) {
        Type type = loaderClass.getGenericInterfaces()[0];
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments()[0] == idType;
        }
        return false;
    }

    @Override
    public String toString() {
        return "{key=" + key + ", idType=" + idType + '}';
    }

}
