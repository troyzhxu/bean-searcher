package cn.zhxu.bs.convertor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class DateFieldConvertorTestCase {

    DateFieldConvertor convertor = new DateFieldConvertor();

    DateFormatFieldConvertor.Formatter dateFormatter = new DateFormatFieldConvertor().new Formatter("yyyy-MM-dd");
    DateFormatFieldConvertor.Formatter dateTimeFormatter = new DateFormatFieldConvertor().new Formatter("yyyy-MM-dd HH:mm:ss");

    @Test
    public void java_util_Date() {
        Object value = new Date(122, Calendar.FEBRUARY, 10, 12, 1, 0);
        Assertions.assertEquals(dateTimeFormatter.format(convertor.convert(Date.class, value)), "2022-02-10 12:01:00");
        Assertions.assertEquals(dateFormatter.format(convertor.convert(java.sql.Date.class, value)), "2022-02-10");
        Assertions.assertEquals(dateTimeFormatter.format(convertor.convert(Timestamp.class, value)), "2022-02-10 12:01:00");
        Assertions.assertEquals(dateTimeFormatter.format(convertor.convert(LocalDateTime.class, value)), "2022-02-10 12:01:00");
        Assertions.assertEquals(dateFormatter.format(convertor.convert(LocalDate.class, value)), "2022-02-10");
    }

    @Test
    public void java_sql_Date() {
        Object value = new java.sql.Date(122, Calendar.FEBRUARY, 10);
        Assertions.assertEquals(dateFormatter.format(convertor.convert(Date.class, value)), "2022-02-10");
        Assertions.assertEquals(dateFormatter.format(convertor.convert(java.sql.Date.class, value)), "2022-02-10");
        Assertions.assertEquals(dateTimeFormatter.format(convertor.convert(Timestamp.class, value)), "2022-02-10 00:00:00");
        Assertions.assertEquals(dateTimeFormatter.format(convertor.convert(LocalDateTime.class, value)), "2022-02-10 00:00:00");
        Assertions.assertEquals(dateFormatter.format(convertor.convert(LocalDate.class, value)), "2022-02-10");
    }

    @Test
    public void java_sql_Timestamp() {
        Object value = new java.sql.Timestamp(122, Calendar.FEBRUARY, 10, 12, 1, 0, 0);
        Assertions.assertEquals(dateTimeFormatter.format(convertor.convert(Date.class, value)), "2022-02-10 12:01:00");
        Assertions.assertEquals(dateFormatter.format(convertor.convert(java.sql.Date.class, value)), "2022-02-10");
        Assertions.assertEquals(dateTimeFormatter.format(convertor.convert(Timestamp.class, value)), "2022-02-10 12:01:00");
        Assertions.assertEquals(dateTimeFormatter.format(convertor.convert(LocalDateTime.class, value)), "2022-02-10 12:01:00");
        Assertions.assertEquals(dateFormatter.format(convertor.convert(LocalDate.class, value)), "2022-02-10");
    }

    @Test
    public void java_time_LocalDate() {
        Object value = LocalDate.of(2022, 2, 10);
        Assertions.assertEquals(dateTimeFormatter.format(convertor.convert(Date.class, value)), "2022-02-10 00:00:00");
        Assertions.assertEquals(dateFormatter.format(convertor.convert(java.sql.Date.class, value)), "2022-02-10");
        Assertions.assertEquals(dateTimeFormatter.format(convertor.convert(Timestamp.class, value)), "2022-02-10 00:00:00");
        Assertions.assertEquals(dateTimeFormatter.format(convertor.convert(LocalDateTime.class, value)), "2022-02-10 00:00:00");
        Assertions.assertEquals(dateFormatter.format(convertor.convert(LocalDate.class, value)), "2022-02-10");
    }

    @Test
    public void java_time_LocalDateTime() {
        Object value = LocalDateTime.of(2022, 2, 10, 12, 1, 0);
        Assertions.assertEquals(dateTimeFormatter.format(convertor.convert(Date.class, value)), "2022-02-10 12:01:00");
        Assertions.assertEquals(dateFormatter.format(convertor.convert(java.sql.Date.class, value)), "2022-02-10");
        Assertions.assertEquals(dateTimeFormatter.format(convertor.convert(Timestamp.class, value)), "2022-02-10 12:01:00");
        Assertions.assertEquals(dateTimeFormatter.format(convertor.convert(LocalDateTime.class, value)), "2022-02-10 12:01:00");
        Assertions.assertEquals(dateFormatter.format(convertor.convert(LocalDate.class, value)), "2022-02-10");
    }

}
