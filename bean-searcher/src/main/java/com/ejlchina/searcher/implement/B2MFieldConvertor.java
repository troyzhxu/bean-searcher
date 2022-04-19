package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BFieldConvertor to MFieldConvertor
 *
 * @author Troy.Zhou @ 2022-04-19
 * @since v3.6.0
 */
public class B2MFieldConvertor implements FieldConvertor.MFieldConvertor {

    private final List<BFieldConvertor> convertors;

    private final Map<Key, BFieldConvertor> cache = new ConcurrentHashMap<>();

    public B2MFieldConvertor(List<BFieldConvertor> convertors) {
        this.convertors = convertors;
    }

    static class Key {

        FieldMeta meta;
        Class<?> valueType;

        public Key(FieldMeta meta, Class<?> valueType) {
            this.meta = meta;
            this.valueType = valueType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equals(meta, key.meta) && Objects.equals(valueType, key.valueType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(meta, valueType);
        }
    }

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        Key key = new Key(meta, valueType);
        BFieldConvertor convertor = cache.get(key);
        if (convertor != null) {
            return true;
        }
        if (cache.containsKey(key)) {
            return false;
        }
        for (BFieldConvertor c: convertors) {
            if (c.supports(meta, valueType)) {
                cache.put(key, c);
                return true;
            }
        }
        cache.put(key, null);
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        Key key = new Key(meta, value.getClass());
        BFieldConvertor convertor = cache.get(key);
        if (convertor != null) {
            return convertor.convert(meta, value);
        }
        throw new IllegalStateException("必须先调用 supports 方法，并且返回 true 后才能调用该方法");
    }

}
