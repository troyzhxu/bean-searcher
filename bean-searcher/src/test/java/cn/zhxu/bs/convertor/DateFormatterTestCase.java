package cn.zhxu.bs.convertor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertTrue(dateFormatter.supports(Date.class));
        Assertions.assertTrue(dateFormatter.supports(java.sql.Date.class));
        Assertions.assertTrue(dateFormatter.supports(Timestamp.class));
        Assertions.assertTrue(dateFormatter.supports(LocalDateTime.class));
        Assertions.assertTrue(dateFormatter.supports(LocalDate.class));
        Assertions.assertFalse(dateFormatter.supports(Time.class));
        Assertions.assertFalse(dateFormatter.supports(LocalTime.class));
    }

    @Test
    public void dateTimeSupports() {
        Assertions.assertTrue(dateTimeFormatter.supports(Date.class));
        Assertions.assertTrue(dateTimeFormatter.supports(Timestamp.class));
        Assertions.assertTrue(dateTimeFormatter.supports(LocalDateTime.class));
        Assertions.assertFalse(dateTimeFormatter.supports(java.sql.Date.class));
        Assertions.assertFalse(dateTimeFormatter.supports(LocalDate.class));
        Assertions.assertFalse(dateTimeFormatter.supports(Time.class));
        Assertions.assertFalse(dateTimeFormatter.supports(LocalTime.class));
    }

    @Test
    public void timeSupports() {
        Assertions.assertTrue(timeFormatter.supports(Date.class));
        Assertions.assertTrue(timeFormatter.supports(Timestamp.class));
        Assertions.assertTrue(timeFormatter.supports(LocalDateTime.class));
        Assertions.assertTrue(timeFormatter.supports(Time.class));
        Assertions.assertTrue(timeFormatter.supports(LocalTime.class));
        Assertions.assertFalse(timeFormatter.supports(java.sql.Date.class));
        Assertions.assertFalse(timeFormatter.supports(LocalDate.class));
    }

    @Test
    public void testTimeFormatter() {
        Object date = new Date(122, Calendar.FEBRUARY, 10, 13, 59, 10);
        Object timestamp = new Timestamp(122, Calendar.FEBRUARY, 10, 13, 59, 10, 0);
        Object localDateTime = LocalDateTime.of(2022, 2, 10, 13, 59, 10);
        Object time = new Time(13, 59, 10);
        Object localTime = LocalTime.of(13, 59, 10);
        Assertions.assertEquals("13:59:10", timeFormatter.format(date));
        Assertions.assertEquals("13:59:10", timeFormatter.format(timestamp));
        Assertions.assertEquals("13:59:10", timeFormatter.format(localDateTime));
        Assertions.assertEquals("13:59:10", timeFormatter.format(time));
        Assertions.assertEquals("13:59:10", timeFormatter.format(localTime));
    }

}
