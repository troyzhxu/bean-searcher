package cn.zhxu.bs.convertor;

import cn.zhxu.bs.BeanMeta;
import cn.zhxu.bs.FieldConvertor;
import cn.zhxu.bs.FieldMeta;
import cn.zhxu.bs.IllegalParamException;
import cn.zhxu.bs.bean.DbField;
import cn.zhxu.bs.bean.DbType;
import cn.zhxu.bs.bean.SearchBean;
import cn.zhxu.bs.implement.DefaultMetaResolver;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;

public class DbFieldsConvertorsTestCase {

    private final FieldConvertor.ParamConvertor[] convertors = new FieldConvertor.ParamConvertor[] {
            new EnumParamConvertor(),
            new NumberParamConvertor(),
            new TimeParamConvertor(),
            new BoolParamConvertor(),
            new DateParamConvertor(),
            new DateTimeParamConvertor(),
    };

    @SearchBean(fields = {
        @DbField(name = "d01", value = "v01"),
        @DbField(name = "d02", value = "v02", type = DbType.BOOL),
        @DbField(name = "d03", value = "v03", type = DbType.BYTE),
        @DbField(name = "d04", value = "v04", type = DbType.SHORT),
        @DbField(name = "d05", value = "v05", type = DbType.INT),
        @DbField(name = "d06", value = "v06", type = DbType.LONG),
        @DbField(name = "d07", value = "v07", type = DbType.FLOAT),
        @DbField(name = "d08", value = "v08", type = DbType.DOUBLE),
        @DbField(name = "d09", value = "v09", type = DbType.DECIMAL),
        @DbField(name = "d10", value = "v10", type = DbType.STRING),
        @DbField(name = "d11", value = "v11", type = DbType.JSON),
        @DbField(name = "d12", value = "v12", type = DbType.DATE),
        @DbField(name = "d13", value = "v13", type = DbType.TIME),
        @DbField(name = "d14", value = "v14", type = DbType.DATETIME),
        @DbField(name = "d15", value = "v15", type = DbType.UNKNOWN)
    })
    public static class Bean {
        private int p01;
        private boolean p02;
        private byte p03;
        private float p04;
        private double p05;
        private long p06;
        private short p07;
        private Integer p08;
        private Boolean p09;
        private Byte p10;
        private Float p11;
        private Double p12;
        private Long p13;
        private Short p14;
        private BigDecimal p15;
        private BigInteger p16;
        private String p17;
        private Date p18;
        private Time p19;
        private Timestamp p20;
        private LocalDate p21;
        private LocalDateTime p22;
        private LocalTime p23;
        private Gender gender;
        @DbField(type = DbType.INT)
        private Gender gender2;
    }

    enum Gender {
        Male,
        Female
    }

    private Object[] values = new Object[] {
        100, true, false, 10f, 10d, "100", "2024-01-16",
        new Date(), LocalTime.now(), LocalDate.now(), LocalDateTime.now(),
        Gender.Female, Gender.Male
    };

    @Test
    public void doTest() {
        BeanMeta<Bean> beanMeta = new DefaultMetaResolver().resolve(Bean.class);
        Collection<FieldMeta> fieldMetas = beanMeta.getFieldMetas();
        for (FieldMeta meta : fieldMetas) {
            for (FieldConvertor.ParamConvertor convertor : convertors) {
                doTest(meta, convertor);
            }
        }
        System.out.println("filed count = " + fieldMetas.size());
    }

    private void doTest(FieldMeta meta, FieldConvertor.ParamConvertor convertor) {
        for (Object value : values) {
            if (convertor.supports(meta, value.getClass())) {
                try {
                    Object v = convertor.convert(meta, value);
                    System.out.println("v = " + v);
                } catch (IllegalParamException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

}
