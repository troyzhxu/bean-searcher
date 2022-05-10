package com.example.demo;

import com.ejlchina.searcher.implement.DateFieldConvertor;
import com.example.demo.entity.Department;
import com.example.demo.entity.Gender;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;


@SpringBootApplication
@MapperScan("com.example.demo.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public DateFieldConvertor dateFieldConvertor() {
        return new DateFieldConvertor() {
    
            @Override
            public Object convert(Class<?> targetType, Object value) {
                Class<?> valueType = value.getClass();
                if (Date.class.isAssignableFrom(valueType)) {
                    Date date = (Date) value;
                    if (targetType == java.sql.Date.class) {
                        return new java.sql.Date(date.getTime());
                    }
                    if (targetType == Timestamp.class) {
                        return new Timestamp(date.getTime());
                    }
                    if (targetType == LocalDateTime.class) {
                        // 注意：java.sql.Date 的 toInstant() 方法会抛异常
                        if (date instanceof java.sql.Date) {
                            LocalDate localDate = ((java.sql.Date) date).toLocalDate();
                            return LocalDateTime.of(localDate, LocalTime.of(0, 0, 0, 0));
                        }
                        return LocalDateTime.ofInstant(date.toInstant(), getZoneId());
                    }
                    if (targetType == LocalDate.class) {
                        // 注意：java.sql.Date 的 toInstant() 方法会抛异常
                        if (date instanceof java.sql.Date) {
                            return ((java.sql.Date) date).toLocalDate();
                        }
                        return toLocalDate(date.toInstant());
                    }
                    if (targetType == Date.class) {
                        return date;
                    }
                }
                LocalDateTime dateTime;
                if (valueType == LocalDateTime.class) {
                    dateTime = (LocalDateTime) value;
                } else {
                    dateTime = LocalDateTime.of((LocalDate) value, LocalTime.of(0, 0));
                }
                if (targetType == LocalDateTime.class) {
                    return dateTime;
                }
                Instant instant = dateTime.atZone(getZoneId()).toInstant();
                if (targetType == Date.class) {
                    return new Date(instant.toEpochMilli());
                }
                if (targetType == java.sql.Date.class) {
                    return new java.sql.Date(instant.toEpochMilli());
                }
                if (targetType == Timestamp.class) {
                    return new Timestamp(instant.toEpochMilli());
                }
                if (targetType == LocalDate.class) {
                    return toLocalDate(instant);
                }
                throw new UnsupportedOperationException();
            }
    
            private LocalDate toLocalDate(Instant instant) {
                ZoneOffset offset = getZoneId().getRules().getOffset(instant);
                long localSecond = instant.getEpochSecond() + offset.getTotalSeconds();
                long localEpochDay = Math.floorDiv(localSecond, 86400);
                return LocalDate.ofEpochDay(localEpochDay);
            }
    
        };
    }

    // 生成数据
    public static void main2(String[] args) {
//        SpringApplication.run(Application.class, args);
        Department[] departments = new Department[] {
            new Department(1, "0101", "Finance-01"),
            new Department(2, "0102", "Finance-02"),
            new Department(3, "0103", "Finance-03"),
            new Department(4, "0104", "Finance-04"),
            new Department(5, "0105", "Finance-05"),
            new Department(6, "0201", "Technical-01"),
            new Department(7, "0202", "Technical-02"),
            new Department(8, "0203", "Technical-03"),
            new Department(9, "0204", "Technical-04"),
            new Department(10, "0205", "Technical-05"),
            new Department(11, "0301", "Market-1"),
            new Department(12, "0302", "Market-2"),
            new Department(13, "0303", "Market-3"),
            new Department(14, "0304", "Market-4"),
            new Department(15, "0305", "Market-5"),
            new Department(16, "0401", "Manager-1"),
            new Department(17, "0402", "Manager-2"),
            new Department(18, "0403", "Manager-3"),
            new Department(19, "0404", "Manager-4"),
            new Department(20, "0405", "Manager-5"),
            new Department(21, "0501", "Engineering-1"),
            new Department(22, "0502", "Engineering-2"),
            new Department(23, "0503", "Engineering-3"),
            new Department(24, "0504", "Engineering-4"),
            new Department(25, "0505", "Engineering-5"),
            new Department(26, "0601", "Maintenance-1"),
            new Department(27, "0602", "Maintenance-2"),
            new Department(28, "0603", "Maintenance-3"),
            new Department(29, "0604", "Maintenance-4"),
            new Department(30, "0605", "Maintenance-5")
        };
        for (Department department : departments) {
            generateEmployee(department);
        }
    }

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static void generateEmployee(Department dept) {
        for (int i = 0; i < 100; i++) {
            int id = (dept.getId() - 1) * 100 + i + 1;
            String jobNo = dept.getCode() + code(i + 1);
            String name = generateName();
            int age = random.nextInt(30) + 18;
            Gender gender = random.nextInt(2) == 0 ? Gender.Female : Gender.Male;
            LocalDateTime entryDate = genEntryDate();
            int deptId = dept.getId();
            LocalDate birthday = genBirthday(age);
            String post = genPost(dept.getName());
            String salary = random.nextInt(30000) + 3000 + ".00";
            String address = genAddress();
            System.out.println("(" + id + ", '" + jobNo + "', '" + name + "', " + age + ", '" + gender + "', '" + formatter.format(entryDate) + "', "
             + deptId + ", '" + formatter.format(birthday) + "', '" + post + "', '" + salary + "', '" + address + "'),");
        }
    }

    private static String genAddress() {
        int length = random.nextInt(40) + 30;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) (random.nextInt(26) + 'a'));
        }
        return sb.toString();
    }

    private static String genPost(String deptName) {
        String name = deptName.substring(0, deptName.indexOf('-'));
        int level = random.nextInt(6);
        return level + name;
    }

    static Random random = new Random();

    static String generateName() {
        int length = random.nextInt(10) + 3;
        StringBuilder sb = new StringBuilder();
        sb.append((char) (random.nextInt(26) + 'A'));
        for (int i = 1; i < length; i++) {
            sb.append((char) (random.nextInt(26) + 'a'));
        }
        return sb.toString();
    }

    static LocalDateTime genEntryDate() {
        int year = random.nextInt(10) + 2010;
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1;
        int hour = random.nextInt(8) + 8;
        int minute = random.nextInt(60);
        return LocalDateTime.of(year, month, day, hour, minute);
    }

    static LocalDate genBirthday(int age) {
        int year = 2022 - age;
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1;
        return LocalDate.of(year, month, day);
    }

    static String code(int v) {
        if (v > 99) {
            return String.valueOf(v);
        }
        if (v > 9) {
            return "0" + v;
        }
        return "00" + v;
    }


}
