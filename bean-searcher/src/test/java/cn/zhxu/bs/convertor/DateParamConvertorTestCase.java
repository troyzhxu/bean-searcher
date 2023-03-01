package cn.zhxu.bs.convertor;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import cn.zhxu.bs.bean.Cluster;
import org.junit.Assert;
import org.junit.Test;

import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.bean.DbType;

public class DateParamConvertorTestCase {

    final DateParamConvertor convertor = new DateParamConvertor();

    @Test
    public void test_support() {
        assertSupports(DbType.BOOL, false);
        assertSupports(DbType.BYTE, false);
        assertSupports(DbType.SHORT, false);
        assertSupports(DbType.INT, false);
        assertSupports(DbType.LONG, false);
        assertSupports(DbType.FLOAT, false);
        assertSupports(DbType.DOUBLE, false);
        assertSupports(DbType.DECIMAL, false);
        assertSupports(DbType.STRING, false);
        assertSupports(DbType.DATE, true);
        assertSupports(DbType.TIME, false);
        assertSupports(DbType.DATETIME, false);
        assertSupports(DbType.UNKNOWN, false);
    }

    void assertSupports(DbType dbType, boolean supports) {
        final FieldMeta meta = new FieldMeta(null, null, null, null, null, false, null, dbType, Cluster.AUTO);
        Assert.assertEquals(supports, convertor.supports(meta, Date.class));
        Assert.assertEquals(supports, convertor.supports(meta, LocalDate.class));
        Assert.assertEquals(supports, convertor.supports(meta, Timestamp.class));
        Assert.assertEquals(supports, convertor.supports(meta, String.class));
    }

    @Test
    public void test_convert() throws ParseException {
        assertConvert("2022-06-16");
        assertConvert("2022-06-16 20:39");
        assertConvert("2022-06-16 20:39:00");
        assertConvert("2022/06/16");
        assertConvert("2022/06/16 20:39");
        assertConvert("2022/06/16 20:39:00");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time = sdf.parse("2022-06-16 20:39:00").getTime();
        assertConvert(new Date(time));
        assertConvert(new Timestamp(time));
        assertConvert(LocalDate.parse("2022-06-16"));
        assertConvert(LocalDate.of(2022, 6, 16));
    }

    private void assertConvert(Object value) {
        FieldMeta meta = new FieldMeta(null, null, null, null, null, false, null, DbType.DATE, Cluster.AUTO);
        Object date = convertor.convert(meta, value);
        Assert.assertTrue(date instanceof java.sql.Date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((java.sql.Date) date);
        Assert.assertEquals(2022, calendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.JUNE, calendar.get(Calendar.MONTH));
        Assert.assertEquals(16, calendar.get(Calendar.DAY_OF_MONTH));
    }

}
