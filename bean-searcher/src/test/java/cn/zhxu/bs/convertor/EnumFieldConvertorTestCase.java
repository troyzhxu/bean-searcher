package cn.zhxu.bs.convertor;

import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.implement.DefaultMetaResolver;
import org.junit.Assert;
import org.junit.Test;

public class EnumFieldConvertorTestCase {

    final EnumFieldConvertor convertor = new EnumFieldConvertor();

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
        Assert.assertTrue(convertor.supports(meta, String.class));
        Assert.assertTrue(convertor.supports(meta, Integer.class));
        Assert.assertTrue(convertor.supports(meta, int.class));
        System.out.println("EnumFieldConvertorTestCase test_supports passed");
    }

    @Test
    public void test_convert_string() {
        Assert.assertEquals(Gender.Male, convertor.convert(meta, "Male"));
        Assert.assertEquals(Gender.Female, convertor.convert(meta, "Female"));
        Assert.assertEquals(Gender.Unknown, convertor.convert(meta, "Unknown"));
        System.out.println("EnumFieldConvertorTestCase test_convert_string passed");
    }

    @Test
    public void test_convert_string_ic() {
        convertor.setIgnoreCase(true);
        Assert.assertEquals(Gender.Male, convertor.convert(meta, "male"));
        Assert.assertEquals(Gender.Female, convertor.convert(meta, "female"));
        Assert.assertEquals(Gender.Unknown, convertor.convert(meta, "unknown"));
        Assert.assertEquals(Gender.Male, convertor.convert(meta, "MALE"));
        Assert.assertEquals(Gender.Female, convertor.convert(meta, "FEMALE"));
        Assert.assertEquals(Gender.Unknown, convertor.convert(meta, "UNKNOWN"));
        System.out.println("EnumFieldConvertorTestCase test_convert_string_ic passed");
    }

    @Test
    public void test_convert_int() {
        Assert.assertEquals(Gender.Male, convertor.convert(meta, 0));
        Assert.assertEquals(Gender.Female, convertor.convert(meta, 1));
        Assert.assertEquals(Gender.Unknown, convertor.convert(meta, 2));
        System.out.println("EnumFieldConvertorTestCase test_convert_int passed");
    }

    @Test
    public void test_fail_on_error() {
        convertor.setFailOnError(true);
        boolean exception = false;
        try {
            Assert.assertEquals(Gender.Male, convertor.convert(meta, "xxx"));
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        Assert.assertTrue(exception);
        exception = false;
        try {
            Assert.assertEquals(Gender.Female, convertor.convert(meta, 3));
        } catch (IllegalArgumentException e) {
            exception = true;
        }
        Assert.assertTrue(exception);
        System.out.println("EnumFieldConvertorTestCase test_fail_on_error passed");
    }

    @Test
    public void test_null_on_error() {
        convertor.setFailOnError(false);
        convertor.setIgnoreCase(false);
        Assert.assertNull(convertor.convert(meta, "male"));
        Assert.assertNull(convertor.convert(meta, "female"));
        Assert.assertNull(convertor.convert(meta, "unknown"));
        Assert.assertNull(convertor.convert(meta, "MALE"));
        Assert.assertNull(convertor.convert(meta, "FEMALE"));
        Assert.assertNull(convertor.convert(meta, "UNKNOWN"));
        Assert.assertNull(convertor.convert(meta, "yyy"));
        Assert.assertNull(convertor.convert(meta, -1));
        Assert.assertNull(convertor.convert(meta, 3));
        System.out.println("EnumFieldConvertorTestCase test_fail_on_error_false passed");
    }

}
