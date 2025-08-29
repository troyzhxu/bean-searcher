package cn.zhxu.bs.ex;

import cn.zhxu.bs.util.StringUtils;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Objects;


public class ExportField {

    private final ExprComputer computer;
    private final Field field;
    private final Export export;

    public ExportField(ExprComputer computer, Field field, Export export) {
        this.computer = computer;
        this.field = Objects.requireNonNull(field);
        this.export = Objects.requireNonNull(export);
    }

    public int idx() {
        return export.idx();
    }

    public String name() {
        return export.name();
    }

    public String withFormat(Object obj) {
        Object value = convert(reflect(obj), obj);
        String format = export.format();
        if (StringUtils.isBlank(format)) {
            return value == null ? "" : value.toString();
        }
        if (value instanceof Date) {
            return new SimpleDateFormat(format).format((Date) value);
        }
        if (value instanceof TemporalAccessor) {
            return DateTimeFormatter.ofPattern(format).format((TemporalAccessor) value);
        }
        if (value instanceof Number) {
            return new DecimalFormat(format).format(value);
        }
        return String.format(format, value);
    }

    private Object reflect(Object obj) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new ExportException("Can not access " + field.getName(), e);
        }
    }

    private Object convert(Object value, Object obj) {
        String expr = export.expr();
        if (computer == null || StringUtils.isBlank(expr)) {
            return value;
        }
        try {
            return computer.compute(expr, obj, value);
        } catch (Exception e) {
            throw new IllegalStateException("Can not compute the expr [" + expr + "]", e);
        }
    }

}
