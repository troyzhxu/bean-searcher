package com.ejlchina.searcher.convertor;

import com.ejlchina.searcher.bean.DbType;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class DateParamConvertorTestCase {

    final DateParamConvertor convertor = new DateParamConvertor();

    @Test
    public void test_support() {
        Assert.assertTrue(convertor.supports(DbType.DATE, Date.class));
        Assert.assertTrue(convertor.supports(DbType.DATE, LocalDate.class));
        Assert.assertTrue(convertor.supports(DbType.DATE, Timestamp.class));
        Assert.assertTrue(convertor.supports(DbType.DATE, String.class));
    }

    @Test
    public void test_convert() {
        assertDate(convertor.convert(DbType.DATE, "2022-06-16"));
        assertDate(convertor.convert(DbType.DATE, "2022-06-16 20:39"));
        assertDate(convertor.convert(DbType.DATE, "2022-06-16 20:39:00"));
        assertDate(convertor.convert(DbType.DATE, "2022/06/16"));
        assertDate(convertor.convert(DbType.DATE, "2022/06/16 20:39"));
        assertDate(convertor.convert(DbType.DATE, "2022/06/16 20:39:00"));
    }

    private void assertDate(Object value) {
        Assert.assertTrue(value instanceof java.sql.Date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((java.sql.Date) value);
        Assert.assertEquals(2022, calendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.JUNE, calendar.get(Calendar.MONTH));
        Assert.assertEquals(16, calendar.get(Calendar.DAY_OF_MONTH));
    }

}
