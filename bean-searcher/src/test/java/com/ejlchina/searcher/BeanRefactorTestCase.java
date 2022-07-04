package com.ejlchina.searcher;

import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.convertor.*;
import com.ejlchina.searcher.implement.DefaultBeanReflector;
import com.ejlchina.searcher.implement.DefaultMetaResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class BeanRefactorTestCase {

    public static class TestBean {
        @DbField(alias = "id")
        private long id;
        @DbField(alias = "name")
        private String name;
        @DbField(alias = "age")
        private int age;
        @DbField(alias = "attrs", conditional = false, converter = Json2MapConverter.class)
        private Map<String,String> attrs;
        @DbField(alias = "dateCreated")
        private Date dateCreated;

        @Override
        public String toString() {
            return "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    ", attrs=" + attrs +
                    ", dateCreated=" + format.format(dateCreated);
        }

        public void assertEqual(Map<String, Object> values) throws Exception {
            assertEquals(values.get("id"), id);
            assertEquals(values.get("name"), name);
            assertEquals(values.get("age"), age);
            assertEquals(values.get("attrs"), attrs);
            assertEquals(values.get("dateCreated"), dateCreated);
        }

        private void assertEquals(Object expect, Object value) throws Exception {
            if (expect instanceof String) {
                if (value instanceof Map) {
                    Assert.assertEquals(expect, Json2MapConverter.OBJECT_MAPPER.writeValueAsString(value));
                } else {
                    Assert.assertEquals(expect, String.valueOf(value));
                }
            } else if (expect instanceof Number && value instanceof Number) {
                Assert.assertEquals(((Number) expect).longValue(), ((Number) value).longValue());
            } else if (value instanceof Date) {
                if (expect instanceof Date) {
                    Assert.assertEquals(expect, value);
                }
                if (expect instanceof LocalDateTime) {
                    Assert.assertEquals(((LocalDateTime) expect).getYear(), ((Date) value).getYear() + 1900);
                    Assert.assertEquals(((LocalDateTime) expect).getMonth().getValue(), ((Date) value).getMonth() + 1);
                    Assert.assertEquals(((LocalDateTime) expect).getDayOfMonth(), ((Date) value).getDate());
                    Assert.assertEquals(((LocalDateTime) expect).getHour(), ((Date) value).getHours());
                    Assert.assertEquals(((LocalDateTime) expect).getMinute(), ((Date) value).getMinutes());
                    Assert.assertEquals(((LocalDateTime) expect).getSecond(), ((Date) value).getSeconds());
                }
            } else {
                Assert.assertEquals(expect, value);
            }
        }

    }

    final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    final MetaResolver metaResolver = new DefaultMetaResolver();

    final BeanReflector beanReflector = new DefaultBeanReflector(Arrays.asList(
            new NumberFieldConvertor(),
            new StrNumFieldConvertor(),
            new BoolFieldConvertor(),
            new DateFieldConvertor(),
            new TimeFieldConvertor(),
            new EnumFieldConvertor()
    ));

    @Test
    public void test() throws Exception {
        BeanMeta<TestBean> beanMeta = metaResolver.resolve(TestBean.class);
        Collection<FieldMeta> fieldSet = beanMeta.getFieldMetas();

        Map<String, Object> values1 = new HashMap<>();
        values1.put("id", 1);
        values1.put("name", "Tom");
        values1.put("age", 20);
        values1.put("dateCreated", new Date());

        beanReflector.reflect(beanMeta, fieldSet, values1::get).assertEqual(values1);

        Map<String,String> attrs = new HashMap<>();
        attrs.put("tel", "1234567890");
        attrs.put("address", "中国江西");

        Map<String, Object> values2 = new HashMap<>();
        values2.put("id", "100");
        values2.put("name", "Tom");
        values2.put("age", 20L);
        values2.put("attrs", Json2MapConverter.OBJECT_MAPPER.writeValueAsString(attrs));
        values2.put("dateCreated", LocalDateTime.now());

        beanReflector.reflect(beanMeta, fieldSet, values2::get).assertEqual(values2);
        System.out.println("BeanRefactorTests OK!");
    }

    //--

    public static class Json2MapConverter implements FieldConvertor.BFieldConvertor {
        private static final TypeReference<Map<String,String>> MAP_STRING_TYPE = new TypeReference<Map<String,String>>() {};
        private static final TypeReference<Map<String,Object>> MAP_OBJECT_TYPE = new TypeReference<Map<String,Object>>() {};

        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        @Override
        public boolean supports(FieldMeta meta, Class<?> valueType) {
            throw new UnsupportedOperationException(""); //无需实现
        }

        @Override
        public Object convert(FieldMeta meta, Object value) {
            Type[] typeParas = ((ParameterizedType) meta.getField().getGenericType()).getActualTypeArguments();
            try {
                return typeParas[1] == String.class
                        ? OBJECT_MAPPER.readValue((String) value, MAP_STRING_TYPE)
                        : OBJECT_MAPPER.readValue((String) value, MAP_OBJECT_TYPE)
                        ;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
