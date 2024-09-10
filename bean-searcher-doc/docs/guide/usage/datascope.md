# 数据权限

所谓数据权限，就是指是不同的用户，根据其各自数据权限的配置信息，通过同一接口或方法来查询数据时，得到不同数据集的一种机制。

虽然数据权限的定义很明确，但其具体的玩法却多种多样，不同的项目团队，可能采用不用的数据权限机制，而 Bean Searcher 支持自定义这种机制。下文将以某一种特定的玩法举例，具体说明在 Bean Searcher 中如何自定义数据权限。

## 玩法举例

假设每个需要有数据权限的业务表（例如订单表 `order`）都有一个 `owner_id`（所有者 ID）字段，而每个所有者都唯一归属于某一个部门，而部门具有树形层级结构（即有上级部门与下级部分）。

### 关键数据表

* dept - 部门表

关键字段 | 备注
-|-
id | 部门ID
parent_id | 上级部门ID

* user - 用户表

关键字段 | 备注
-|-
id | 用户ID
dept_id | 从属部门ID

* order - 业务数据表

关键字段 | 备注
-|-
id | 订单ID
owner_id | 所有者 ID

### 权限配置项

对于每个用户，配置其某个业务表的数据权限时，可以有以下四个选项：

1. `ONLY_SELF` - 只能查看属于自己的数据
2. `SELF_DEPT` - 可以查看属于自己部门的数据
3. `SUB_DEPTS` 可以查看属于自己部门以及所有子部门的数据
4. `ALL` - 可以查询所有数据

## 玩法实现

### 自定义注解

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DataScope {
    /**
     * 用于指定数据权限作用在哪张表上（因为可能是联表查询）
     */
    String on() default "";
}
```

该注解用于标记哪些 **检索实体类** 需要启用数据权限。

### 注解使用说明

```java
@SearchBean(
  tables = "order o, xxx x",     // 假设需要联表查询
  where = "o.xxx_id = x.id <ds>" // 占位符 <ds> 用于标记需要插入数据权限条件的地方
)
@DataScope(on = "o")             // 启用数据权限, 并且作用在 order 表上
public class OrderVO {
    ...
}
```

这里有一个技巧，就是使用一个占位符（这里用 `<ds>`）来标记需要插入数据权限条件的地方，这样我们的自定义代码就不需用再引入其它框架来解析 SQL 语法了，这样我们的代码简单，性能也非常高。

### 自定义 SQL 拦截器

```java
@Component
public class DataScopeSqlInterceptor implements SqlInterceptor {

    @Override
    public <T> SearchSql<T> intercept(SearchSql<T> searchSql, Map<String, Object> paraMap, FetchType fetchType) {
        // 获取 @DataScope 注解
        DataScope dataScope = searchSql.getBeanMeta().getBeanClass().getAnnotation(DataScope.class);
        if (dataScope == null) {
            // 如果没有启用数据权限，则直接返回
            return searchSql;
        }
        // 处理列表查询 SQL 的数据权限
        searchSql.setListSqlString(process(dataScope, searchSql.getListSqlString()));
        // 处聚组表查询 SQL 的数据权限
        searchSql.setClusterSqlString(process(dataScope, searchSql.getClusterSqlString()));
        return searchSql;
    }

    static final String PLACEHOLDER = "<ds>";   // 占位符

    private String process(DataScope dataScope, String sql) {
        int index = sql.indexOf(PLACEHOLDER);
        if (index < 0) {
            // 没有占位符，直接返回
            return sql;
        }
        // 占位符 之前的 SQL
        String preSql = sql.substring(0, index);
        // 占位符 之后的 SQL
        String nexSql = sql.substring(index + PLACEHOLDER.length());
        // 获取当前请求者的信息（不同的项目架构，这里使用的方法都不一样）
        PrincipalUser user = PrincipalHolder.requireUser();

    }

}
```



