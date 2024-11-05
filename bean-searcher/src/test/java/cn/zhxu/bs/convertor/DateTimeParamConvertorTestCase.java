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
        System.out.println("\ttest_support ok!");
    }

    void assertSupports(DbType dbType, boolean supports) {
        FieldMeta meta = new FieldMeta(null,  null, null, null, null, false, null, dbType, Cluster.AUTO);
        Assertions.assertEquals(supports, convertor.supports(meta, Date.class));
        Assertions.assertEquals(supports, convertor.supports(meta, LocalDate.class));
        Assertions.assertEquals(supports, convertor.supports(meta, LocalDateTime.class));
        Assertions.assertEquals(supports, convertor.supports(meta, String.class));
        Assertions.assertEquals(supports, convertor.supports(meta, Long.class));
    }

    @Test
    public void test_convert() throws ParseException {
        assertConvert("2022-06-08", 0, 0, 0);
        assertConvert("2022-06-08 20:39", 20, 39, 0);
        assertConvert("2022-06-08 20:39:10", 20, 39, 10);
        assertConvert("2022-06-08 20:39:10", 20, 39, 10);
        assertConvert("2022/06/08", 0, 0, 0);
        assertConvert("2022/06/08 20:39", 20, 39, 0);
        assertConvert("2022/06/08 20:39:10", 20, 39, 10);
        assertConvert("2022/06/08 20:39:10.666", 20, 39, 10, 666);
        assertConvert("2022-6-08", 0, 0, 0);
        assertConvert("2022-6-08 20:39", 20, 39, 0);
        assertConvert("2022-6-8 20:39:10", 20, 39, 10);
        assertConvert("2022-6-8 20:39:10", 20, 39, 10);
        assertConvert("2022/6/08", 0, 0, 0);
        assertConvert("2022/6/08 20:39", 20, 39, 0);
        assertConvert("2022/6/8 20:39:10", 20, 39, 10);
        assertConvert("2022/6/8 20:39:10.6", 20, 39, 10, 6);
        assertConvert("2022-6-08", 0, 0, 0);
        assertConvert("2022-6-08 6:09", 6, 9, 0);
        assertConvert("2022-06-8 20:3:10", 20, 3, 10);
        assertConvert("2022-06-8 2:9:10", 2, 9, 10);
        assertConvert("2022-06-8 2:9:8", 2, 9, 8);
        assertConvert("2022-6-8 02:09:8", 2, 9, 8);
        assertConvert("2022-6-8 02:09:8.55", 2, 9, 8, 55);
        assertConvert("2022-6-8 02:09:8.999", 2, 9, 8, 999);
        assertConvert("2022/6/08", 0, 0, 0);
        assertConvert("2022/6/08 6:09", 6, 9, 0);
        assertConvert("2022/6/8 20:3:10", 20, 3, 10);
        assertConvert("2022/6/8 2:9:10", 2, 9, 10);
        assertConvert("2022/06/8 2:9:8", 2, 9, 8);
        assertConvert("2022/06/8 02:09:8", 2, 9, 8);
        assertConvert("2022/06/8 02:09:8.55", 2, 9, 8, 55);
        assertConvert("2022/6/8 02:09:8.999", 2, 9, 8, 999);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time = sdf.parse("2022-06-08 20:39:10").getTime();
        assertConvert(time, 20, 39, 10);
        assertConvert(String.valueOf(time), 20, 39, 10);
        assertConvert(new Date(time), 20, 39, 10);
        assertConvert(new Timestamp(time), 20, 39, 10);
        assertConvert(LocalDate.parse("2022-06-08"), 0, 0, 0);
        assertConvert(LocalDate.of(2022, 6, 8), 0, 0, 0);
        assertConvert(LocalDateTime.parse("2022-06-08T20:39:10"), 20, 39, 10);
        assertConvert(LocalDateTime.of(2022, 6, 8, 20, 39, 10), 20, 39, 10);
        System.out.println("\ttest_convert ok!");
    }

    private void assertConvert(Object value, int hour, int minutes, int seconds) {
        assertConvert(value, hour, minutes, seconds, 0);
    }

    private void assertConvert(Object value, int hour, int minutes, int seconds, int mills) {
        FieldMeta meta = new FieldMeta(null, null,null, null, null, false, null, DbType.DATETIME, Cluster.AUTO);
        Object date = convertor.convert(meta, value);
        Assertions.assertInstanceOf(Timestamp.class, date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((java.sql.Timestamp) date);
        Assertions.assertEquals(2022, calendar.get(Calendar.YEAR));
        Assertions.assertEquals(Calendar.JUNE, calendar.get(Calendar.MONTH));
        Assertions.assertEquals(8, calendar.get(Calendar.DAY_OF_MONTH));
        Assertions.assertEquals(hour, calendar.get(Calendar.HOUR_OF_DAY));
        Assertions.assertEquals(minutes, calendar.get(Calendar.MINUTE));
        Assertions.assertEquals(seconds, calendar.get(Calendar.SECOND));
        Assertions.assertEquals(mills, calendar.get(Calendar.MILLISECOND));
    }

}
