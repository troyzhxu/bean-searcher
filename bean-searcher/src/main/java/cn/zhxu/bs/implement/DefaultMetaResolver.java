package cn.zhxu.bs.implement;

import cn.zhxu.bs.*;
import cn.zhxu.bs.bean.InheritType;
import cn.zhxu.bs.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
            @SuppressWarnings("unchecked")
            BeanMeta<T> meta = (BeanMeta<T>) cache.get(beanClass);
            if (meta == null) {
                meta = resolveMetadata(beanClass);
                cache.put(beanClass, meta);
            }
            return meta;
        }
    }

    @Override
    public void clearCache() {
        synchronized (cache) {
            cache.clear();
        }
    }

    public static class FieldWrapper {

        public final Field field;
        public final DbMapping.Column column;

        public FieldWrapper(Field field, DbMapping.Column column) {
            if (field != null) {
                field.setAccessible(true);
            }
            this.field = field;
            this.column = column;
        }
    }

    protected <T> BeanMeta<T> resolveMetadata(Class<T> beanClass) {
        DbMapping.Table table = dbMapping.table(beanClass);
        if (table == null) {
            throw new SearchException("The class [" + beanClass.getName() + "] can not be searched, because it can not be resolved by " + dbMapping.getClass());
        }
        BeanMeta<T> beanMeta = createBeanMeta(beanClass, table);
        List<FieldWrapper> wrappers = new ArrayList<>();
        table.getFields().forEach(column -> wrappers.add(new FieldWrapper(null, column)));
        wrappers.addAll(getBeanFields(beanClass).stream()
                .map(field -> {
                    // 解析实体类字段
                    DbMapping.Column column = dbMapping.column(beanClass, field);
                    return column != null ? new FieldWrapper(field, column) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        // 不再对 wrappers 进行排序，以保持用户在实体类中的字段声明顺序

        Set<String> fieldChecks = new HashSet<>();  // 用于校验属性是否重复
        Set<String> aliasChecks = new HashSet<>();  // 用于校验别名是否重复

        for (FieldWrapper wrapper: wrappers) {
            if (fieldChecks.contains(wrapper.column.getName())) {
                throw new SearchException("Duplicate field name [" + wrapper.column.getName() + "] on [" + beanClass.getName() + "].");
            } else {
                fieldChecks.add(wrapper.column.getName());
            }
            // 在自动别名生成之前对指定的别名进行校验，并放入校验集
            String alias = wrapper.column.getAlias();
            if (StringUtils.isNotBlank(alias)) {
                if (aliasChecks.contains(alias)) {
                    throw new SearchException("The alias [" + alias + "] of [" + beanClass.getName()
                            + "." + wrapper.column.getName() + "] is already exists on other fields.");
                }
                aliasChecks.add(alias);
            }
        }
        for (FieldWrapper wrapper: wrappers) {
            String alias = resolveAlias(wrapper.column, aliasChecks);
            SqlSnippet fieldSql = snippetResolver.resolve(wrapper.column.getFieldSql());
            FieldMeta fieldMeta = new FieldMeta(
                    beanMeta, wrapper.column.getName(),
                    wrapper.field, fieldSql, alias,
                    wrapper.column.isConditional(),
                    wrapper.column.getOnlyOn(),
                    wrapper.column.getDbType(),
                    wrapper.column.getCluster()
            );
            beanMeta.addFieldMeta(fieldMeta);
        }
        if (beanMeta.getSelectFields().isEmpty()) {
            throw new SearchException("[" + beanClass.getName() + "] is not a valid SearchBean, because there is no field mapping to database. Please refer https://bs.zhxu.cn/guide/latest/bean.html#%E7%9C%81%E7%95%A5-dbfield for help.");
        }
        return beanMeta;
    }

    protected <T> BeanMeta<T> createBeanMeta(Class<T> beanClass, DbMapping.Table table) {
        return new BeanMeta<>(beanClass, table.getDataSource(),
                snippetResolver.resolve(table.getTables()),
                snippetResolver.resolve(table.getWhere()),
                snippetResolver.resolve(table.getGroupBy()),
                snippetResolver.resolve(table.getHaving()),
                snippetResolver.resolve(table.getOrderBy()),
                table.isSortable(), table.isDistinct(),
                table.getTimeout(), table.getMaxSize(),
                table.getMaxOffset());
    }

    protected String resolveAlias(DbMapping.Column column, Set<String> checks) {
        String alias = column.getAlias();
        if (StringUtils.isBlank(alias)) {
            // 注意：Oracle 数据库的别名不能以下划线开头
            int index = checks.size();
            alias = "c_" + index;
            while (checks.contains(alias)) {
                alias = "c_" + (++index);
            }
            checks.add(alias);
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
