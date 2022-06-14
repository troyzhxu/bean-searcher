package com.ejlchina.searcher.convertor;

import com.ejlchina.searcher.FieldConvertor;
import com.ejlchina.searcher.FieldMeta;
import com.ejlchina.searcher.util.ObjKey2;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BFieldConvertor to MFieldConvertor
 *
 * @author Troy.Zhou @ 2022-04-19
 * @since v3.6.0（v3.8.0 之前在 com.ejlchina.searcher.implement 包下）
 */
public class B2MFieldConvertor implements FieldConvertor.MFieldConvertor {

    private final List<BFieldConvertor> convertors;

    private final Map<ObjKey2, BFieldConvertor> cache = new ConcurrentHashMap<>();

    static final BFieldConvertor NULL_CONVERTOR = (meta, valueType) -> false;

    public B2MFieldConvertor(List<BFieldConvertor> convertors) {
        this.convertors = convertors;
    }

    @Override
    public boolean supports(FieldMeta meta, Class<?> valueType) {
        ObjKey2 key = new ObjKey2(meta, valueType);
        BFieldConvertor convertor = cache.get(key);
        if (convertor == NULL_CONVERTOR) {
            return false;
        }
        if (convertor != null) {
            return true;
        }
        for (BFieldConvertor c: convertors) {
            if (c.supports(meta, valueType)) {
                cache.put(key, c);
                return true;
            }
        }
        cache.put(key, NULL_CONVERTOR);
        return false;
    }

    @Override
    public Object convert(FieldMeta meta, Object value) {
        ObjKey2 key = new ObjKey2(meta, value.getClass());
        BFieldConvertor convertor = cache.get(key);
        if (convertor != null) {
            return convertor.convert(meta, value);
        }
        throw new IllegalStateException("The supports(FieldMeta, Class<?>) method must be called first and return true before convert(FieldMeta, Object) method can be called");
    }

}
