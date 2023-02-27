package cn.zhxu.bs.convertor;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.bean.DbType;

public class DateTimeParamConvertorTestCase {

    final DateTimeParamConvertor convertor = new DateTimeParamConvertor();

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
        assertSupports(DbType.DATE, false);
        assertSupports(DbType.TIME, false);
        assertSupports(DbType.DATETIME, true);
        assertSupports(DbType.UNKNOWN, false);
    }

    void assertSupports(DbType dbType, boolean supports) {
        FieldMeta meta = new FieldMeta(null,  null, null, null, null, false, null, dbType);
        Assert.assertEquals(supports, convertor.supports(meta, Date.class));
        Assert.assertEquals(supports, convertor.supports(meta, LocalDate.class));
        Assert.assertEquals(supports, convertor.supports(meta, LocalDateTime.class));
        Assert.assertEquals(supports, convertor.supports(meta, String.class));
    }

    @Test
    public void test_convert() throws ParseException {
        assertConvert("2022-06-16", 0, 0, 0);
        assertConvert("2022-06-16 20:39", 20, 39, 0);
        assertConvert("2022-06-16 20:39:10", 20, 39, 10);
        assertConvert("2022-06-16 20:39:10", 20, 39, 10);
        assertConvert("2022/06/16", 0, 0, 0);
        assertConvert("2022/06/16 20:39", 20, 39, 0);
        assertConvert("2022/06/16 20:39:10", 20, 39, 10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time = sdf.parse("2022-06-16 20:39:10").getTime();
        assertConvert(new Date(time), 20, 39, 10);
        assertConvert(new Timestamp(time), 20, 39, 10);
        assertConvert(LocalDate.parse("2022-06-16"), 0, 0, 0);
        assertConvert(LocalDate.of(2022, 6, 16), 0, 0, 0);
        assertConvert(LocalDateTime.parse("2022-06-16T20:39:10"), 20, 39, 10);
        assertConvert(LocalDateTime.of(2022, 6, 16, 20, 39, 10), 20, 39, 10);
    }

    private void assertConvert(Object value, int hour, int minutes, int seconds) {
        FieldMeta meta = new FieldMeta(null, null,null, null, null, false, null, DbType.DATETIME);
        Object date = convertor.convert(meta, value);
        Assert.assertTrue(date instanceof java.sql.Timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((java.sql.Timestamp) date);
        System.out.println(((java.sql.Timestamp) date).getTime());
        Assert.assertEquals(2022, calendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.JUNE, calendar.get(Calendar.MONTH));
        Assert.assertEquals(16, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(hour, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(minutes, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(seconds, calendar.get(Calendar.SECOND));
    }

}
