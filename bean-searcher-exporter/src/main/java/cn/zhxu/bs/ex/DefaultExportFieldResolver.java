package cn.zhxu.bs.ex;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的导出字段解析器
 * @author Troy.Zhou @ 2025-08-29
 * @since v4.5.0
 */
public class DefaultExportFieldResolver implements ExportFieldResolver {

    private final Map<Class<?>, List<ExportField>> cache = new ConcurrentHashMap<>();
    private final Expresser expresser;
    private final Formatter formatter;

    public DefaultExportFieldResolver() {
        this(null, Formatter.DEFAULT);
    }

    public DefaultExportFieldResolver(Expresser expresser) {
        this(expresser, Formatter.DEFAULT);
    }

    public DefaultExportFieldResolver(Expresser expresser, Formatter formatter) {
        this.expresser = expresser;
        this.formatter = formatter;
    }

    @Override
    public List<ExportField> resolve(Class<?> beanClass) {
        List<ExportField> exportFields = cache.get(beanClass);
        if (exportFields != null) {
            return exportFields;
        }
        synchronized (cache) {
            List<ExportField> fields = cache.get(beanClass);
            if (fields == null) {
                fields = resolveFields(beanClass);
                cache.put(beanClass, fields);
            }
            return fields;
        }
    }

    @Override
    public void clearCache() {
        synchronized (cache) {
            cache.clear();
        }
    }

    public List<ExportField> resolveFields(Class<?> clazz) {
        List<ExportField> exFields = new ArrayList<>();
        Set<String> names = new HashSet<>();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                String name = field.getName();
                if (field.isSynthetic() || Modifier.isStatic(modifiers)
                        || Modifier.isTransient(modifiers)
                        || names.contains(name)) {
                    continue;
                }
                ExportField exField = toExportField(field);
                if (exField != null) {
                    field.setAccessible(true);
                    exFields.add(exField);
                    names.add(name);
                }
            }
            clazz = clazz.getSuperclass();
        }
        exFields.sort(Comparator.comparingInt(ExportField::getExIdx));
        return exFields;
    }

    public ExportField toExportField(Field field) {
        Export export = field.getAnnotation(Export.class);
        if (export == null) {
            return null;
        }
        return new ExportField(
                expresser, formatter, field,
                export.name(),
                export.idx(),
                export.expr(),
                export.format(),
                export.onlyIf()
        );
    }

    public Expresser getExpresser() {
        return expresser;
    }

    public Formatter getFormatter() {
        return formatter;
    }

}
