package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.*;
import com.ejlchina.searcher.bean.InheritType;
import com.ejlchina.searcher.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认元信息解析器
 * @author Troy.Zhou @ 2021-10-30
 * @since v3.0.0
 */
public class DefaultMetaResolver implements MetaResolver {

    private final Map<Class<?>, BeanMeta<?>> cache = new ConcurrentHashMap<>();

    private SnippetResolver snippetResolver = new DefaultSnippetResolver();

    private DbMapping dbMapping;

    public DefaultMetaResolver() {
        this(new DefaultDbMapping());
    }

    public DefaultMetaResolver(DbMapping dbMapping) {
        this.dbMapping = dbMapping;
    }

    @Override
    public <T> BeanMeta<T> resolve(Class<T> beanClass) {
        @SuppressWarnings("unchecked")
        BeanMeta<T> beanMeta = (BeanMeta<T>) cache.get(beanClass);
        if (beanMeta != null) {
            return beanMeta;
        }
        synchronized (cache) {
            beanMeta = resolveMetadata(beanClass);
            cache.put(beanClass, beanMeta);
            return beanMeta;
        }
    }

    public static class FieldWrapper {

        public final Field field;
        public final DbMapping.Column column;

        public FieldWrapper(Field field, DbMapping.Column column) {
            this.field = field;
            this.column = column;
        }
    }

    protected <T> BeanMeta<T> resolveMetadata(Class<T> beanClass) {
        DbMapping.Table table = dbMapping.table(beanClass);
        if (table == null) {
            throw new SearchException("The class [" + beanClass.getName() + "] can not be searched, because it can not be resolved by " + dbMapping.getClass());
        }
        BeanMeta<T> beanMeta = new BeanMeta<>(beanClass, table.getDataSource(),
                snippetResolver.resolve(table.getTables()),
                snippetResolver.resolve(table.getWhere()),
                snippetResolver.resolve(table.getGroupBy()),
                snippetResolver.resolve(table.getHaving()),
                snippetResolver.resolve(table.getOrderBy()),
                table.isSortable(), table.isDistinct());
        // 解析实体类字段
        FieldWrapper[] wrappers = getBeanFields(beanClass).stream()
                .map(field -> {
                    DbMapping.Column column = dbMapping.column(beanClass, field);
                    return column != null ? new FieldWrapper(field, column) : null;
                })
                .filter(Objects::nonNull)
                // 排序：使指定了别名的 字段排在前面
                .sorted((w1, w2) -> {
                    int i1 = StringUtils.isBlank(w1.column.getAlias()) ? 1 : 0;
                    int i2 = StringUtils.isBlank(w2.column.getAlias()) ? 1 : 0;
                    return i1 - i2;
                })
                .toArray(FieldWrapper[]::new);
        // 用于校验别名是否重复
        Set<String> checkSet = new HashSet<>();
        for (FieldWrapper wrapper: wrappers) {
            wrapper.field.setAccessible(true);
            String fieldAlias = resolveAlias(wrapper.column, checkSet);
            if (fieldAlias != null) {
                checkSet.add(fieldAlias);
            } else {
                throw new SearchException("The alias [" + wrapper.column.getAlias() + "] of [" + beanClass.getName()
                        + "." + wrapper.field.getName() + "] is already exists on other fields.");
            }
            SqlSnippet fieldSql = snippetResolver.resolve(wrapper.column.getFieldSql());
            FieldMeta fieldMeta = new FieldMeta(
                    beanMeta, wrapper.field, fieldSql, fieldAlias,
                    wrapper.column.isConditional(),
                    wrapper.column.getOnlyOn(),
                    wrapper.column.getDbType(),
                    wrapper.column.getConvClazz()
            );
            beanMeta.addFieldMeta(wrapper.field.getName(), fieldMeta);
        }
        if (beanMeta.getFieldCount() == 0) {
            throw new SearchException("[" + beanClass.getName() + "] is not a valid SearchBean, because there is no field mapping to database.\n" +
                    "Please refer https://bs.zhxu.cn/guide/latest/bean.html#%E7%9C%81%E7%95%A5-dbfield for help.");
        }
        return beanMeta;
    }

    protected String resolveAlias(DbMapping.Column column, Set<String> checkSet) {
        String alias = column.getAlias();
        if (StringUtils.isBlank(alias)) {
            // 注意：Oracle 数据库的别名不能以下划线开头
            int index = checkSet.size();
            alias = "c_" + index;
            while (checkSet.contains(alias)) {
                alias = "c_" + (++index);
            }
            return alias;
        }
        if (checkSet.contains(alias)) {
            return null;
        }
        return alias;
    }

    protected List<Field> getBeanFields(Class<?> beanClass) {
        InheritType iType = dbMapping.inheritType(beanClass);
        List<Field> fieldList = new ArrayList<>();
        Set<String> fieldNames = new HashSet<>();
        while (beanClass != Object.class) {
            for (Field field : beanClass.getDeclaredFields()) {
                String name = field.getName();
                int modifiers = field.getModifiers();
                if (field.isSynthetic() || Modifier.isStatic(modifiers)
                        || Modifier.isTransient(modifiers)
                        || fieldNames.contains(name)) {
                    continue;
                }
                fieldList.add(field);
                fieldNames.add(name);
            }
            if (iType != InheritType.FIELD && iType != InheritType.ALL) {
                break;
            }
            beanClass = beanClass.getSuperclass();
        }
        return fieldList;
    }

    public SnippetResolver getSnippetResolver() {
        return snippetResolver;
    }

    public void setSnippetResolver(SnippetResolver snippetResolver) {
        this.snippetResolver = Objects.requireNonNull(snippetResolver);
    }

    public DbMapping getDbMapping() {
        return dbMapping;
    }

    public void setDbMapping(DbMapping dbMapping) {
        this.dbMapping = Objects.requireNonNull(dbMapping);
    }

}
