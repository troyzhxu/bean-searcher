package cn.zhxu.bs.label;

import java.lang.reflect.Field;

public class LabelField {

    private final Field field;
    private final Field forField;
    private final String key;

    public LabelField(Field field, Field forField, String key) {
        this.field = field;
        this.forField = forField;
        this.key = key;
    }

    public Field getField() {
        return field;
    }

    public Field getForField() {
        return forField;
    }

    public String getKey() {
        return key;
    }

}
