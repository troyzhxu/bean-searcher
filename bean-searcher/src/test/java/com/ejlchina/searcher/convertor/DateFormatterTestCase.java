package com.ejlchina.searcher.convertor;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class DateFormatterTestCase {

    DateFormatFieldConvertor.Formatter dateFormatter = new DateFormatFieldConvertor().new Formatter("yyyy-MM-dd");
    DateFormatFieldConvertor.Formatter dateTimeFormatter = new DateFormatFieldConvertor().new Formatter("yyyy-MM-dd HH:mm:ss");
    DateFormatFieldConvertor.Formatter timeFormatter = new DateFormatFieldConvertor().new Formatter("HH:mm:ss");

    @Test
    public void dateSupports() {
        Assert.assertTrue(dateFormatter.supports(Date.class));
        Assert.assertTrue(dateFormatter.supports(java.sql.Date.class));
        Assert.assertTrue(dateFormatter.supports(Timestamp.class));
        Assert.assertTrue(dateFormatter.supports(LocalDateTime.class));
        Assert.assertTrue(dateFormatter.supports(LocalDate.class));
        Assert.assertFalse(dateFormatter.supports(Time.class));
        Assert.assertFalse(dateFormatter.supports(LocalTime.class));
    }

    @Test
    public void dateTimeSupports() {
        Assert.assertTrue(dateTimeFormatter.supports(Date.class));
        Assert.assertTrue(dateTimeFormatter.supports(Timestamp.class));
        Assert.assertTrue(dateTimeFormatter.supports(LocalDateTime.class));
        Assert.assertFalse(dateTimeFormatter.supports(java.sql.Date.class));
        Assert.assertFalse(dateTimeFormatter.supports(LocalDate.class));
        Assert.assertFalse(dateTimeFormatter.supports(Time.class));
        Assert.assertFalse(dateTimeFormatter.supports(LocalTime.class));
    }

    @Test
    public void timeSupports() {
        Assert.assertTrue(timeFormatter.supports(Date.class));
        Assert.assertTrue(timeFormatter.supports(Timestamp.class));
        Assert.assertTrue(timeFormatter.supports(LocalDateTime.class));
        Assert.assertTrue(timeFormatter.supports(Time.class));
        Assert.assertTrue(timeFormatter.supports(LocalTime.class));
        Assert.assertFalse(timeFormatter.supports(java.sql.Date.class));
        Assert.assertFalse(timeFormatter.supports(LocalDate.class));
    }

    @Test
    public void testTimeFormatter() {
        Object date = new Date(122, Calendar.FEBRUARY, 10, 13, 59, 10);
        Object timestamp = new Timestamp(122, Calendar.FEBRUARY, 10, 13, 59, 10, 0);
        Object localDateTime = LocalDateTime.of(2022, 2, 10, 13, 59, 10);
        Object time = new Time(13, 59, 10);
        Object localTime = LocalTime.of(13, 59, 10);
        Assert.assertEquals("13:59:10", timeFormatter.format(date));
        Assert.assertEquals("13:59:10", timeFormatter.format(timestamp));
        Assert.assertEquals("13:59:10", timeFormatter.format(localDateTime));
        Assert.assertEquals("13:59:10", timeFormatter.format(time));
        Assert.assertEquals("13:59:10", timeFormatter.format(localTime));
    }

}
