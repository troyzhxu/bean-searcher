package cn.zhxu.bs.convertor;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.bean.DbType;

public class NumberParamConvertorTestCase {

    final NumberParamConvertor convertor = new NumberParamConvertor();

    @Test
    public void test_support() {
        assertSupports(DbType.BOOL, false);
        assertSupports(DbType.BYTE, true);
        assertSupports(DbType.SHORT, true);
        assertSupports(DbType.INT, true);
        assertSupports(DbType.LONG, true);
        assertSupports(DbType.FLOAT, true);
        assertSupports(DbType.DOUBLE, true);
        assertSupports(DbType.DECIMAL, true);
        assertSupports(DbType.STRING, false);
        assertSupports(DbType.DATE, false);
        assertSupports(DbType.TIME, false);
        assertSupports(DbType.DATETIME, false);
        assertSupports(DbType.UNKNOWN, false);
    }

    void assertSupports(DbType dbType, boolean supports) {
        FieldMeta meta = new FieldMeta(null, null, null, null, false, null, dbType);
        Assert.assertEquals(supports, convertor.supports(meta, String.class));
        Assert.assertEquals(supports, convertor.supports(meta, Byte.class));
        Assert.assertEquals(supports, convertor.supports(meta, Short.class));
        Assert.assertEquals(supports, convertor.supports(meta, Integer.class));
        Assert.assertEquals(supports, convertor.supports(meta, Long.class));
        Assert.assertEquals(supports, convertor.supports(meta, Float.class));
        Assert.assertEquals(supports, convertor.supports(meta, Double.class));
        Assert.assertEquals(supports, convertor.supports(meta, BigDecimal.class));
    }

    @Test
    public void test_convert() {
        assertConvert((byte) 100);
        assertConvert((short) 100);
        assertConvert(100);
        assertConvert(100L);
        assertConvert(100f);
        assertConvert(100d);
        assertConvert(new BigDecimal(100));
        assertConvert("100");
    }

    private void assertConvert(Object value) {
        assertConvert(DbType.BYTE, value);
        assertConvert(DbType.SHORT, value);
        assertConvert(DbType.INT, value);
        assertConvert(DbType.LONG, value);
        assertConvert(DbType.FLOAT, value);
        assertConvert(DbType.DOUBLE, value);
        assertConvert(DbType.DECIMAL, value);
    }

    private void assertConvert(DbType dbType, Object value) {
        FieldMeta meta = new FieldMeta(null, null, null, null, false, null, dbType);
        Object num = convertor.convert(meta, value);
        Assert.assertTrue(dbType.getType().isInstance(num));
        String numStr = num.toString();
        String valStr = value.toString();
        Assert.assertTrue(numStr.equals(valStr) || numStr.equals(valStr + ".0") || valStr.equals(numStr + ".0"));
    }

}
