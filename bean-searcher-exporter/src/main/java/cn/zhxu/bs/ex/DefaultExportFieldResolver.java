package cn.zhxu.bs.ex;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
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
        if (clazz.isRecord()) {
            // record 类：按 canonical constructor 参数顺序遍历组件
            for (RecordComponent component : clazz.getRecordComponents()) {
                Field field;
                try {
                    field = clazz.getDeclaredField(component.getName());
                } catch (NoSuchFieldException e) {
                    continue;
                }
                // 优先取 RecordComponent 上的注解，降级到 Field 上
                Export export = component.getAnnotation(Export.class);
                if (export == null) {
                    export = field.getAnnotation(Export.class);
                }
                if (export != null) {
                    field.setAccessible(true);
                    exFields.add(new ExportField(
                            expresser, formatter, field,
                            export.name(), export.idx(),
                            export.expr(), export.format(), export.onlyIf()
                    ));
                }
            }
        } else {
            // 普通类：遍历继承链（遇到 Object 或 Record 停止）
            Set<String> names = new HashSet<>();
            while (clazz != Object.class && clazz != Record.class) {
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
