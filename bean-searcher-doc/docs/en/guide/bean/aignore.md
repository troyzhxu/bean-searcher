# 注解缺省

Bean Searcher 自 v3.0 起开始支持注解省略。

## 省略 @SearchBean

当 Bean Searcher 找不到 `@SearchBean` 注解（v3.2 开始会自动寻找父类的 `@SearchBean` 注解），或 `@SearchBean` 注解内没有指定 `tables` 属性时，会认为该实体类是一个 **单表映射** 实体类。此时的表名将服从自动映射规则：

* `表名` =  `前缀` + `根据配置是否转为大写（驼峰转小写下划线（去掉冗余后缀的类名））`

其中的 `前缀` 与 `根据配置是否转为大写` 是一个可配置项，可使用以下方式配置。

### SpringBoot / Grails

使用 `bean-searcher-boot-starter` 依赖时，可通过以下键名配置：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.sql.default-mapping.table-prefix` | 表名前缀 | `字符串` | `null`
`bean-searcher.sql.default-mapping.underline-case` | 表名和字段名是否驼峰转小写下划线（since v3.7.0） | `布尔值` | `true`
`bean-searcher.sql.default-mapping.upper-case` | 表名和字段名是否大写 | `布尔值` | `false`
`bean-searcher.sql.default-mapping.redundant-suffixes` | 类名的冗余后缀（可配多个）（since v3.3.0） | `冗余后缀` | `null`

::: tip 冗余后缀
例如冗余后缀配置为 VO,DTO 时，则对于名为 `UserVO`, `UserDTO` 的实体类, 在自动映射表名是，会自动将 VO，DTO 后缀给去掉。
:::

### 非 Boot 的 Spring 项目

```xml
<bean id="dbMapping" class="cn.zhxu.bs.implement.DefaultDbMapping">
    <property name="tablePrefix" value="t_" />      <!-- 表名前缀 -->
    <property name="underlineCase" value="true" />  <!-- 是否驼峰转小写下划线 -->
    <property name="upperCase" value="false" />     <!-- 是否大写 -->
</bean>
<bean id="metaResolver" class="cn.zhxu.bs.implement.DefaultMetaResolver">
    <property name="dbMapping" ref="dbMapping" />
</bean>
<bean id="mapSearcher" class="cn.zhxu.bs.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="metaResolver" ref="metaResolver" />
</bean>
```

### 其它框架

```java
DefaultDbMapping dbMapping = new DefaultDbMapping();
dbMapping.setTablePrefix("t_");     // 表名前缀
dbMapping.setUpperCase(false);      // 是否大写
dbMapping.setUnderlineCase(true);   // 是否驼峰转小写下划线

MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它配置
        .metaResolver(new DefaultMetaResolver(dbMapping))   // BeanSearcher 检索器也同此配置
        .build();
```

## 省略 @DbField

当检索实体类满足以下四个条件之一时（满足一个即可）：

* 实体类省略了 `@SearchBean` 注解
* 实体类的 `@SearchBean` 没有指定 `tables` 属性
* 实体类的 `@SearchBean.tables` 只含一张表（since v3.8.0）
* 实体类的 `@SearchBean` 指定了 `autoMapTo` 属性

则实体类中省略 `@DbField` 注解 且没被 `@DbIgnore` 注解的字段将会自动映射到数据库，自动映射规则为：

* `数据库字段名` = `根据配置是否转为大写（驼峰转小写下划线（实体类字段名））`
* 如果实体类指定了 `autoMapTo` 属性，则该字段映射到 `autoMapTo` 指定的表中

其中的 `根据配置是否转为大写` 是一个可配置项，配置方法 [同上文](/en/guide/bean/aignore#省略-searchbean)。

::: tip 提示
如果想忽略某个字段，可使用 `@DbIgnore` 注解，它不可与 `@DbField` 在同一个字段上使用。
:::

## 识别其它 ORM 的注解

例如你已经在项目中使用了 Jpa，那么你可能希望 Bean Searcher 自动识别 Jpa 的注解。这很简单，如果你使用的是 `bean-searcher-boot-starter` 或 `bean-searcher-solon-plugin` 依赖，则只需声明一个 Bean 即可：

::: code-group
```java [v4.3.5+]
@Bean
public DbMapping bsJpaDbMapping(BeanSearcherSql config) {
    var mapping = new DefaultDbMapping() {

        @Override
        public String toTableName(Class<?> beanClass) {
            // 识别 JPA 的 @Table 注解
            var table = beanClass.getAnnotation(javax.persistence.Table.class);
            if (table != null && StringUtils.notBlank(table.name())) {
                return table.name();
            }
            // 识别 JPA 的 @Entity 注解
            var entity = beanClass.getAnnotation(javax.persistence.Entity.class);
            if (entity != null && StringUtils.notBlank(entity.name())) {
                return entity.name();
            }
            return super.toTableName(beanClass);
        }

        @Override
        public String toColumnName(BeanField field) {
            // 识别 JPA 的 @Column 注解
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
            // 识别 JPA 的 @Table 注解
            var table = beanClass.getAnnotation(javax.persistence.Table.class);
            if (table != null && StringUtils.notBlank(table.name())) {
                return table.name();
            }
            // 识别 JPA 的 @Entity 注解
            var entity = beanClass.getAnnotation(javax.persistence.Entity.class);
            if (entity != null && StringUtils.notBlank(entity.name())) {
                return entity.name();
            }
            return super.toTableName(beanClass);
        }

        @Override
        public String toColumnName(BeanField field) {
            // 识别 JPA 的 @Column 注解
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

如果你用的是其它 ORM，则只需要简单修改 `String toTableName(Class<?> beanClass)` 与 `String toColumnName(BeanField field)` 方法里的代码即可。
