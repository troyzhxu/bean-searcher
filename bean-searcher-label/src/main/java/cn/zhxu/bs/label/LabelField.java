package cn.zhxu.bs.label;

import java.lang.reflect.Field;
import java.util.Map;

public class LabelField {

    private final Field field;
    private final Field idField;

    public LabelField(Field field, Field idField) {
        this.field = field;
        this.idField = idField;
    }

    public static class KEY {

        private final String key;
        private final Field field;
        private final Field idField;

        public KEY(Field field, Field idField, String key) {
            this.field = field;
            this.idField = idField;
            this.key = key;
        }

        public LabelKey key() {
            Class<?> idType = idField.getType();
            if (idType == boolean.class) {
                return new LabelKey(key, Boolean.class);
            }
            if (idType == byte.class) {
                return new LabelKey(key, Byte.class);
            }
            if (idType == char.class) {
                return new LabelKey(key, Character.class);
            }
            if (idType == short.class) {
                return new LabelKey(key, Short.class);
            }
            if (idType == int.class) {
                return new LabelKey(key, Integer.class);
            }
            if (idType == float.class) {
                return new LabelKey(key, Float.class);
            }
            if (idType == long.class) {
                return new LabelKey(key, Long.class);
            }
            if (idType == double.class) {
                return new LabelKey(key, Double.class);
            }
            return new LabelKey(key, idType);
        }

        public LabelField field() {
            return new LabelField(field, idField);
        }

    }

    public Object id(Object data) {
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
