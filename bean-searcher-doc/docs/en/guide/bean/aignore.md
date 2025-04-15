# Default Annotation Omission

Bean Searcher has supported annotation omission since version 3.0.

## Omitting @SearchBean

When Bean Searcher cannot find the `@SearchBean` annotation (starting from version 3.2, it will automatically search for the `@SearchBean` annotation in the parent class), or the `tables` attribute is not specified in the `@SearchBean` annotation, it will consider the entity class as a **single-table mapping** entity class. In this case, the table name will follow the automatic mapping rule:

* `Table Name` = `Prefix` + `Convert to lowercase underscore according to configuration (remove redundant suffixes from the class name and convert camel case)`

The `Prefix` and `Convert to uppercase according to configuration` are configurable items, which can be configured in the following ways.

### SpringBoot / Grails

When using the `bean-searcher-boot-starter` dependency, you can configure it through the following key names:

Configuration Key Name | Meaning | Optional Values | Default Value
-|-|-|-
`bean-searcher.sql.default-mapping.table-prefix` | Table name prefix | `String` | `null`
`bean-searcher.sql.default-mapping.underline-case` | Whether to convert table names and field names from camel case to lowercase underscore (since v3.7.0) | `Boolean` | `true`
`bean-searcher.sql.default-mapping.upper-case` | Whether to convert table names and field names to uppercase | `Boolean` | `false`
`bean-searcher.sql.default-mapping.redundant-suffixes` | Redundant suffixes of the class name (multiple can be configured) (since v3.3.0) | `Redundant Suffix` | `null`

::: tip Redundant Suffix
For example, when the redundant suffixes are configured as VO and DTO, for entity classes named `UserVO` and `UserDTO`, the VO and DTO suffixes will be automatically removed during automatic table name mapping.
:::

### Non-Boot Spring Projects

```xml
<bean id="dbMapping" class="cn.zhxu.bs.implement.DefaultDbMapping">
    <property name="tablePrefix" value="t_" />      <!-- Table name prefix -->
    <property name="underlineCase" value="true" />  <!-- Whether to convert camel case to lowercase underscore -->
    <property name="upperCase" value="false" />     <!-- Whether to convert to uppercase -->
</bean>
<bean id="metaResolver" class="cn.zhxu.bs.implement.DefaultMetaResolver">
    <property name="dbMapping" ref="dbMapping" />
</bean>
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- Other property configurations are omitted, and the BeanSearcher retriever is configured in the same way -->
    <property name="metaResolver" ref="metaResolver" />
</bean>
```

### Other Frameworks

```java
DefaultDbMapping dbMapping = new DefaultDbMapping();
dbMapping.setTablePrefix("t_");     // Table name prefix
dbMapping.setUpperCase(false);      // Whether to convert to uppercase
dbMapping.setUnderlineCase(true);   // Whether to convert camel case to lowercase underscore

MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // Other configurations are omitted
        .metaResolver(new DefaultMetaResolver(dbMapping))   // The BeanSearcher retriever is configured in the same way
        .build();
```

## Omitting @DbField

When the retrieval entity class meets one of the following four conditions (only one needs to be met):

* The `@SearchBean` annotation is omitted from the entity class.
* The `tables` attribute is not specified in the `@SearchBean` of the entity class.
* The `@SearchBean.tables` of the entity class contains only one table (since v3.8.0).
* The `autoMapTo` attribute is specified in the `@SearchBean` of the entity class.

Then, fields in the entity class that omit the `@DbField` annotation and are not annotated with `@DbIgnore` will be automatically mapped to the database. The automatic mapping rule is:

* `Database Field Name` = `Convert to uppercase according to configuration (convert camel case to lowercase underscore (entity class field name))`
* If the `autoMapTo` attribute is specified in the entity class, the field will be mapped to the table specified by `autoMapTo`.

The `Convert to uppercase according to configuration` is a configurable item, and the configuration method is [the same as above](/en/guide/bean/aignore#Omitting-@SearchBean).

::: tip Tip
If you want to ignore a certain field, you can use the `@DbIgnore` annotation. It cannot be used on the same field as `@DbField`.
:::

## Recognizing Annotations of Other ORMs

For example, if you have already used Jpa in your project, you may want Bean Searcher to automatically recognize Jpa annotations. This is very simple. If you are using the `bean-searcher-boot-starter` or `bean-searcher-solon-plugin` dependency, you only need to declare a Bean:

::: code-group
```java [v4.3.5+]
@Bean
public DbMapping bsJpaDbMapping(BeanSearcherSql config) {
    var mapping = new DefaultDbMapping() {

        @Override
        public String toTableName(Class<?> beanClass) {
            // Recognize JPA's @Table annotation
            var table = beanClass.getAnnotation(javax.persistence.Table.class);
            if (table != null && StringUtils.notBlank(table.name())) {
                return table.name();
            }
            // Recognize JPA's @Entity annotation
            var entity = beanClass.getAnnotation(javax.persistence.Entity.class);
            if (entity != null && StringUtils.notBlank(entity.name())) {
                return entity.name();
            }
            return super.toTableName(beanClass);
        }

        @Override
        public String toColumnName(BeanField field) {
            // Recognize JPA's @Column annotation
            var column = field.getAnnotation(javax.persistence.Column.class);
            if (column != null && StringUtils.notBlank(column.name())) {
                return column.name();
            }
            return super.toColumnName(field);
        }

    };
    BeanSearcherSql.DefaultMapping conf = config.getDefaultMapping();
    mapping.setTablePrefix(conf.getTablePrefix());
    mapping.setUpperCase(conf.isUpperCase());
    mapping.setUnderlineCase(conf.isUnderlineCase());
    mapping.setRedundantSuffixes(conf.getRedundantSuffixes());
    mapping.setIgnoreFields(conf.getIgnoreFields());
    mapping.setDefaultInheritType(conf.getInheritType());
    mapping.setDefaultSortType(conf.getSortType());
    return mapping;
}
```

```java [v4.3.4-]
@Bean
public DbMapping bsJpaDbMapping(BeanSearcherProperties config) {
    var mapping = new DefaultDbMapping() {

        @Override
        public String toTableName(Class<?> beanClass) {
            // Recognize JPA's @Table annotation
            var table = beanClass.getAnnotation(javax.persistence.Table.class);
            if (table != null && StringUtils.notBlank(table.name())) {
                return table.name();
            }
            // Recognize JPA's @Entity annotation
            var entity = beanClass.getAnnotation(javax.persistence.Entity.class);
            if (entity != null && StringUtils.notBlank(entity.name())) {
                return entity.name();
            }
            return super.toTableName(beanClass);
        }

        @Override
        public String toColumnName(BeanField field) {
            // Recognize JPA's @Column annotation
            var column = field.getAnnotation(javax.persistence.Column.class);
            if (column != null && StringUtils.notBlank(column.name())) {
                return column.name();
            }
            return super.toColumnName(field);
        }

    };
    BeanSearcherProperties.Sql.DefaultMapping conf = config.getSql().getDefaultMapping();
    mapping.setTablePrefix(conf.getTablePrefix());
    mapping.setUpperCase(conf.isUpperCase());
    mapping.setUnderlineCase(conf.isUnderlineCase());
    mapping.setRedundantSuffixes(conf.getRedundantSuffixes());
    mapping.setIgnoreFields(conf.getIgnoreFields());
    mapping.setDefaultInheritType(conf.getInheritType());
    mapping.setDefaultSortType(conf.getSortType());
    return mapping;
}
```
:::

If you are using other ORMs, you only need to simply modify the code in the `String toTableName(Class<?> beanClass)` and `String toColumnName(BeanField field)` methods.
