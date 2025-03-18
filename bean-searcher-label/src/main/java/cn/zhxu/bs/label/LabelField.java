package cn.zhxu.bs.label;

import java.lang.reflect.Field;
import java.util.Map;

public class LabelField {

    private final Field field;
    private final Field idField;
    private final String key;

    public LabelField(Field field, Field idField, String key) {
        this.field = field;
        this.idField = idField;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public Object getId(Object data) {
        if (data instanceof Map) {
            return ((Map<?, ?>) data).get(idField.getName());
        }
        try {
            return idField.get(data);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void setLabel(Object data, String label) {
        if (data instanceof Map) {
            ((Map<String, Object>) data).put(field.getName(), label);
            return;
        }
        try {
            field.set(data, label);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
