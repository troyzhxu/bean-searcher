package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.implement.DefaultMetaResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumFieldConvertorTestCase {

    final FieldMeta meta = new DefaultMetaResolver().resolve(Model.class).requireFieldMeta("gender");

    static class Model {
        Gender gender;
    }

    enum Gender {
        Male,
        Female,
        Unknown
    }

    @Test
    public void test_supports() {
        EnumFieldConvertor convertor = new EnumFieldConvertor();
        Assertions.assertTrue(convertor.supports(meta, String.class));
        Assertions.assertTrue(convertor.supports(meta, Integer.class));
        Assertions.assertTrue(convertor.supports(meta, int.class));
        System.out.println("\ttest_support ok!");
    }

    @Test
    public void test_convert_string() {
        EnumFieldConvertor convertor = new EnumFieldConvertor();
        Assertions.assertEquals(Gender.Male, convertor.convert(meta, "Male"));
        Assertions.assertEquals(Gender.Female, convertor.convert(meta, "Female"));
        Assertions.assertEquals(Gender.Unknown, convertor.convert(meta, "Unknown"));
        System.out.println("\ttest_convert_string ok!");
    }

    @Test
    public void test_convert_string_ic() {
        EnumFieldConvertor convertor = new EnumFieldConvertor();
        convertor.setIgnoreCase(true);
        Assertions.assertEquals(Gender.Male, convertor.convert(meta, "male"));
        Assertions.assertEquals(Gender.Female, convertor.convert(meta, "female"));
        Assertions.assertEquals(Gender.Unknown, convertor.convert(meta, "unknown"));
        Assertions.assertEquals(Gender.Male, convertor.convert(meta, "MALE"));
        Assertions.assertEquals(Gender.Female, convertor.convert(meta, "FEMALE"));
        Assertions.assertEquals(Gender.Unknown, convertor.convert(meta, "UNKNOWN"));
        System.out.println("\ttest_convert_string_ic ok!");
    }

    @Test
    public void test_convert_int() {
        EnumFieldConvertor convertor = new EnumFieldConvertor();
        Assertions.assertEquals(Gender.Male, convertor.convert(meta, 0));
        Assertions.assertEquals(Gender.Female, convertor.convert(meta, 1));
        Assertions.assertEquals(Gender.Unknown, convertor.convert(meta, 2));
        System.out.println("\ttest_convert_int ok!");
    }

    @Test
    public void test_fail_on_error() {
        EnumFieldConvertor convertor = new EnumFieldConvertor();
        convertor.setFailOnError(true);
        boolean exception = false;
        try {
            Assertions.assertEquals(Gender.Male, convertor.convert(meta, "xxx"));
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        Assertions.assertTrue(exception);
        exception = false;
        try {
            Assertions.assertEquals(Gender.Female, convertor.convert(meta, 3));
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        Assertions.assertTrue(exception);
        System.out.println("\ttest_fail_on_error ok!");
    }

    @Test
    public void test_null_on_error() {
        EnumFieldConvertor convertor = new EnumFieldConvertor();
        convertor.setFailOnError(false);
        convertor.setIgnoreCase(false);
        Assertions.assertNull(convertor.convert(meta, "male"));
        Assertions.assertNull(convertor.convert(meta, "female"));
        Assertions.assertNull(convertor.convert(meta, "unknown"));
        Assertions.assertNull(convertor.convert(meta, "MALE"));
        Assertions.assertNull(convertor.convert(meta, "FEMALE"));
        Assertions.assertNull(convertor.convert(meta, "UNKNOWN"));
        Assertions.assertNull(convertor.convert(meta, "yyy"));
        Assertions.assertNull(convertor.convert(meta, -1));
        Assertions.assertNull(convertor.convert(meta, 3));
        System.out.println("\ttest_null_on_error ok!");
    }

}
