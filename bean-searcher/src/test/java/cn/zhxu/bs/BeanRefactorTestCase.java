package cn.zhxu.bs;

import cn.zhxu.bs.bean.DbField;
import cn.zhxu.bs.convertor.*;
import cn.zhxu.bs.implement.DefaultBeanReflector;
import cn.zhxu.bs.implement.DefaultMetaResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        @DbField(alias = "dateCreated")
        private Date dateCreated;

        @Override
        public String toString() {
            return "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    ", dateCreated=" + format.format(dateCreated);
        }

        public void assertEqual(Map<String, Object> values) {
            assertEquals(values.get("id"), id);
            assertEquals(values.get("name"), name);
            assertEquals(values.get("age"), age);
            assertEquals(values.get("dateCreated"), dateCreated);
        }

        private void assertEquals(Object expect, Object value) {
            if (expect instanceof String) {
                Assertions.assertEquals(expect, String.valueOf(value));
            } else if (expect instanceof Number && value instanceof Number) {
                Assertions.assertEquals(((Number) expect).longValue(), ((Number) value).longValue());
            } else if (value instanceof Date) {
                if (expect instanceof Date) {
                    Assertions.assertEquals(expect, value);
                }
                if (expect instanceof LocalDateTime) {
                    Assertions.assertEquals(((LocalDateTime) expect).getYear(), ((Date) value).getYear() + 1900);
                    Assertions.assertEquals(((LocalDateTime) expect).getMonth().getValue(), ((Date) value).getMonth() + 1);
                    Assertions.assertEquals(((LocalDateTime) expect).getDayOfMonth(), ((Date) value).getDate());
                    Assertions.assertEquals(((LocalDateTime) expect).getHour(), ((Date) value).getHours());
                    Assertions.assertEquals(((LocalDateTime) expect).getMinute(), ((Date) value).getMinutes());
                    Assertions.assertEquals(((LocalDateTime) expect).getSecond(), ((Date) value).getSeconds());
                }
            } else {
                Assertions.assertEquals(expect, value);
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
    public void test() {
        BeanMeta<TestBean> beanMeta = metaResolver.resolve(TestBean.class);
        Collection<FieldMeta> fieldSet = beanMeta.getFieldMetas();

        Map<String, Object> values1 = new HashMap<>();
        values1.put("id", 1);
        values1.put("name", "Tom");
        values1.put("age", 20);
        values1.put("dateCreated", new Date());

        beanReflector.reflect(beanMeta, fieldSet, values1::get).assertEqual(values1);

        Map<String, Object> values2 = new HashMap<>();
        values2.put("id", "100");
        values2.put("name", "Tom");
        values2.put("age", 20L);
        values2.put("dateCreated", LocalDateTime.now());

        beanReflector.reflect(beanMeta, fieldSet, values2::get).assertEqual(values2);
        System.out.println("BeanRefactorTests OK!");
    }


}
