package cn.zhxu.bs.convertor;

import cn.zhxu.bs.bean.Cluster;
import org.junit.Assert;
import org.junit.Test;

import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.bean.DbType;

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
        final FieldMeta meta = new FieldMeta(null, null,null, null, null, false, null, dbType, Cluster.AUTO);
        Assert.assertEquals(supports, convertor.supports(meta, Byte.class));
        Assert.assertEquals(supports, convertor.supports(meta, Short.class));
        Assert.assertEquals(supports, convertor.supports(meta, Integer.class));
        Assert.assertEquals(supports, convertor.supports(meta, Long.class));
        Assert.assertEquals(supports, convertor.supports(meta, String.class));
    }


    @Test
    public void test_convert() {
        FieldMeta meta = new FieldMeta(null, null,null, null, null, false, null, DbType.BOOL, Cluster.AUTO);
        Assert.assertEquals(true, convertor.convert(meta, 1));
        Assert.assertEquals(true, convertor.convert(meta, true));
        Assert.assertEquals(true, convertor.convert(meta, "true"));
        Assert.assertEquals(true, convertor.convert(meta, "on"));
        Assert.assertEquals(true, convertor.convert(meta, "TRUE"));
        Assert.assertEquals(true, convertor.convert(meta, "ON"));
        Assert.assertEquals(false, convertor.convert(meta, 0));
        Assert.assertEquals(false, convertor.convert(meta, false));
        Assert.assertEquals(false, convertor.convert(meta, "false"));
        Assert.assertEquals(false, convertor.convert(meta, "f"));
        Assert.assertEquals(false, convertor.convert(meta, "off"));
        Assert.assertEquals(false, convertor.convert(meta, "n"));
        Assert.assertEquals(false, convertor.convert(meta, "no"));
        Assert.assertEquals(false, convertor.convert(meta, "FALSE"));
        Assert.assertEquals(false, convertor.convert(meta, "F"));
        Assert.assertEquals(false, convertor.convert(meta, "OFF"));
        Assert.assertEquals(false, convertor.convert(meta, "N"));
        Assert.assertEquals(false, convertor.convert(meta, "NO"));
    }

}
