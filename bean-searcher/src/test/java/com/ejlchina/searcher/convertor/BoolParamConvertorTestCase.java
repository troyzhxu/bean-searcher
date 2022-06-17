package com.ejlchina.searcher.convertor;

import com.ejlchina.searcher.bean.DbType;
import org.junit.Assert;
import org.junit.Test;

public class BoolParamConvertorTestCase {

    final BoolParamConvertor convertor = new BoolParamConvertor();

    @Test
    public void test_support() {
        assertSupports(DbType.BOOL, true);
        assertSupports(DbType.BYTE, false);
        assertSupports(DbType.SHORT, false);
        assertSupports(DbType.INT, false);
        assertSupports(DbType.LONG, false);
        assertSupports(DbType.FLOAT, false);
        assertSupports(DbType.DOUBLE, false);
        assertSupports(DbType.DECIMAL, false);
        assertSupports(DbType.STRING, false);
        assertSupports(DbType.DATE, false);
        assertSupports(DbType.TIME, false);
        assertSupports(DbType.DATETIME, false);
        assertSupports(DbType.UNKNOWN, false);
    }

    void assertSupports(DbType dbType, boolean supports) {
        Assert.assertEquals(supports, convertor.supports(dbType, Byte.class));
        Assert.assertEquals(supports, convertor.supports(dbType, Short.class));
        Assert.assertEquals(supports, convertor.supports(dbType, Integer.class));
        Assert.assertEquals(supports, convertor.supports(dbType, Long.class));
        Assert.assertEquals(supports, convertor.supports(dbType, String.class));
    }


    @Test
    public void test_convert() {
        Assert.assertEquals(true, convertor.convert(DbType.BOOL, 1));
        Assert.assertEquals(true, convertor.convert(DbType.BOOL, true));
        Assert.assertEquals(true, convertor.convert(DbType.BOOL, "true"));
        Assert.assertEquals(true, convertor.convert(DbType.BOOL, "on"));
        Assert.assertEquals(true, convertor.convert(DbType.BOOL, "TRUE"));
        Assert.assertEquals(true, convertor.convert(DbType.BOOL, "ON"));
        Assert.assertEquals(false, convertor.convert(DbType.BOOL, 0));
        Assert.assertEquals(false, convertor.convert(DbType.BOOL, false));
        Assert.assertEquals(false, convertor.convert(DbType.BOOL, "false"));
        Assert.assertEquals(false, convertor.convert(DbType.BOOL, "f"));
        Assert.assertEquals(false, convertor.convert(DbType.BOOL, "off"));
        Assert.assertEquals(false, convertor.convert(DbType.BOOL, "n"));
        Assert.assertEquals(false, convertor.convert(DbType.BOOL, "no"));
        Assert.assertEquals(false, convertor.convert(DbType.BOOL, "FALSE"));
        Assert.assertEquals(false, convertor.convert(DbType.BOOL, "F"));
        Assert.assertEquals(false, convertor.convert(DbType.BOOL, "OFF"));
        Assert.assertEquals(false, convertor.convert(DbType.BOOL, "N"));
        Assert.assertEquals(false, convertor.convert(DbType.BOOL, "NO"));
    }

}
