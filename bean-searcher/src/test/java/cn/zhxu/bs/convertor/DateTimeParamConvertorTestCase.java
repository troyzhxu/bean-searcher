package cn.zhxu.bs.convertor;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import cn.zhxu.bs.bean.Cluster;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        FieldMeta meta = new FieldMeta(null,  null, null, null, null, false, null, dbType, Cluster.AUTO);
        Assertions.assertEquals(supports, convertor.supports(meta, Date.class));
        Assertions.assertEquals(supports, convertor.supports(meta, LocalDate.class));
        Assertions.assertEquals(supports, convertor.supports(meta, LocalDateTime.class));
        Assertions.assertEquals(supports, convertor.supports(meta, String.class));
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
        FieldMeta meta = new FieldMeta(null, null,null, null, null, false, null, DbType.DATETIME, Cluster.AUTO);
        Object date = convertor.convert(meta, value);
        Assertions.assertTrue(date instanceof java.sql.Timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((java.sql.Timestamp) date);
        System.out.println(((java.sql.Timestamp) date).getTime());
        Assertions.assertEquals(2022, calendar.get(Calendar.YEAR));
        Assertions.assertEquals(Calendar.JUNE, calendar.get(Calendar.MONTH));
        Assertions.assertEquals(16, calendar.get(Calendar.DAY_OF_MONTH));
        Assertions.assertEquals(hour, calendar.get(Calendar.HOUR_OF_DAY));
        Assertions.assertEquals(minutes, calendar.get(Calendar.MINUTE));
        Assertions.assertEquals(seconds, calendar.get(Calendar.SECOND));
    }

}
