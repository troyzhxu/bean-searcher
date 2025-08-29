package cn.zhxu.bs.ex;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 默认的导出字段解析器
 * @author Troy.Zhou @ 2025-08-29
 * @since v4.5.0
 */
public class DefaultExportFieldResolver implements ExportFieldResolver {

    private final ExprComputer exprComputer;

    public DefaultExportFieldResolver() {
        this(null);
    }

    public DefaultExportFieldResolver(ExprComputer exprComputer) {
        this.exprComputer = exprComputer;
    }

    @Override
    public List<ExportField> resolve(Class<?> clazz) {
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
        return new ExportField(exprComputer, field, export.name(), export.idx(), export.expr(), export.format());
    }

}
