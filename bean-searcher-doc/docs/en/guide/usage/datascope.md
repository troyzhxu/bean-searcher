# 数据权限

所谓数据权限，就是指是不同的用户，根据其各自数据权限的配置信息，通过同一接口或方法来查询数据时，得到不同数据集的一种机制。

虽然数据权限的定义很明确，但其具体的规则（玩法、花样）却多种多样，不同的项目团队，可能采用不用的数据权限规则，而 Bean Searcher 支持自定义这种规则。下文将以某一特定的规则举例，具体说明在 Bean Searcher 中如何实现数据权限。

## 规则举例

假设每个需要数据权限的业务表（例如订单表 `order`）都有 `owner_id`（所有者 ID）与 `dept_id`（从属部门ID）字段，而每个所有者都唯一归属于某一个部门，而部门具有树形层级结构。

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
dept_id | 所在部门ID

* order - 业务数据表

关键字段 | 备注
-|-
id | 订单ID
owner_id | 所有者 ID
dept_id | 归属部门ID

### 权限配置项

当配置用户的某个业务的数据权限时（通常配置在角色上），可以有以下四个选项：

1. `ONLY_SELF` - 只能查看属于自己的数据
2. `SELF_DEPT` - 可以查看自己部门的数据
3. `SUB_DEPTS` 查看自己部门及所有子部门的数据
4. `ALL` - 可以查看所有数据

## 代码实现

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

这里有一个技巧，就是使用一个占位符（`<ds>`）来标记需要插入数据权限条件的地方，这样我们的自定义代码就无需引入第三方框架来解析 SQL 语法了，因此我们的代码可以更简单。

### 数据权限拦截器

接着，就可以自定义数据权限拦截器了：

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
        // 获取当前请求者的信息（不同的项目架构，这里使用的方法各不一样）
        PrincipalUser user = PrincipalHolder.requireUser();
        // 数据权限 SQL 条件
        String scopeSql = buildScopeSql(dataScope.on(), user);
        // 拼接新的完整 SQL
        if (scopeSql == null) {
            return preSql + nexSql;
        }
        String trim = preSql.trim().toLowerCase();
        if (trim.endsWith("where") || trim.endsWith("and")) {
            return preSql + " " + scopeSql + " " + nexSql;
        }
        return preSql + " and " + scopeSql + " " + nexSql;
    }

    private String buildScopeSql(String on, PrincipalUser user) {
        // 获取当前用户的数据权限配置类型
        ScopeType scopeType = user.getScopeType();
        // 只查属于自己的数据
        if (scopeType == ScopeType.ONLY_SELF) {
            return ownerId(on) + " = " + user.getId();
        }
        // 只查自己部门的数据
        if (scopeType == ScopeType.SELF_DEPT) {
            return deptId(on) + " = " + user.getDeptId();
        }
        // 查看自己部门以及所有子部门的数据
        if (scopeType == ScopeType.SUB_DEPTS) {
            // 这里使用了递归 SQL 语法，如果数据库引擎不支持该语法
            // 也可以先单独将所有子部门ID 都查出来，然后在此拼接
            return deptId(on) + "in (with recursive d_tree as (" +
                " select id, parent_id from dept" +
                "  where id = " + user.getDeptId() +
                " union all" +
                " select d.id, d.parent_id from dept d, d_tree t" +
                "  where t.id = d.parent_id" +
                ") select id from d_tree)"; 
        }
        // 查看所有数据
        return null;
    }

    private String ownerId(String on) {
        if (StringUtils.isBlank(on)) {
          return "owner_id";
        }
        return on + ".owner_id";
    }

    private String deptId(String on) {
        if (StringUtils.isBlank(on)) {
          return "dept_id";
        }
        return on + ".dept_id";
    }

}
```

以上，就实现了一种数据权限机制，其它类型的数据权限也同理可实现。
