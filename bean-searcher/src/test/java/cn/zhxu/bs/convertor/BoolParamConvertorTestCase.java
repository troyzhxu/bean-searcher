package cn.zhxu.bs.convertor;

import cn.zhxu.bs.bean.Cluster;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertEquals(supports, convertor.supports(meta, Byte.class));
        Assertions.assertEquals(supports, convertor.supports(meta, Short.class));
        Assertions.assertEquals(supports, convertor.supports(meta, Integer.class));
        Assertions.assertEquals(supports, convertor.supports(meta, Long.class));
        Assertions.assertEquals(supports, convertor.supports(meta, String.class));
    }


    @Test
    public void test_convert() {
        FieldMeta meta = new FieldMeta(null, null,null, null, null, false, null, DbType.BOOL, Cluster.AUTO);
        Assertions.assertEquals(true, convertor.convert(meta, 1));
        Assertions.assertEquals(true, convertor.convert(meta, true));
        Assertions.assertEquals(true, convertor.convert(meta, "true"));
        Assertions.assertEquals(true, convertor.convert(meta, "on"));
        Assertions.assertEquals(true, convertor.convert(meta, "TRUE"));
        Assertions.assertEquals(true, convertor.convert(meta, "ON"));
        Assertions.assertEquals(false, convertor.convert(meta, 0));
        Assertions.assertEquals(false, convertor.convert(meta, false));
        Assertions.assertEquals(false, convertor.convert(meta, "false"));
        Assertions.assertEquals(false, convertor.convert(meta, "f"));
        Assertions.assertEquals(false, convertor.convert(meta, "off"));
        Assertions.assertEquals(false, convertor.convert(meta, "n"));
        Assertions.assertEquals(false, convertor.convert(meta, "no"));
        Assertions.assertEquals(false, convertor.convert(meta, "FALSE"));
        Assertions.assertEquals(false, convertor.convert(meta, "F"));
        Assertions.assertEquals(false, convertor.convert(meta, "OFF"));
        Assertions.assertEquals(false, convertor.convert(meta, "N"));
        Assertions.assertEquals(false, convertor.convert(meta, "NO"));
    }

}
