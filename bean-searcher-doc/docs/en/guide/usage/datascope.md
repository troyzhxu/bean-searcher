# Data Permissions

The so - called data permissions refer to a mechanism where different users obtain different data sets when querying data through the same interface or method according to their respective data permission configuration information.

Although the definition of data permissions is clear, its specific rules (ways of playing, variations) are diverse. Different project teams may adopt different data permission rules, and Bean Searcher supports customizing such rules. The following will take a specific rule as an example to illustrate how to implement data permissions in Bean Searcher.

## Rule Example

Assume that each business table requiring data permissions (such as the order table `order`) has fields `owner_id` (owner ID) and `dept_id` (affiliated department ID). Each owner uniquely belongs to a certain department, and departments have a tree - like hierarchical structure.

### Key Data Tables

* dept - Department Table

Key Fields | Remarks
-|-
id | Department ID
parent_id | Parent Department ID

* user - User Table

Key Fields | Remarks
-|-
id | User ID
dept_id | Department ID where the user is located

* order - Business Data Table

Key Fields | Remarks
-|-
id | Order ID
owner_id | Owner ID
dept_id | Affiliated Department ID

### Permission Configuration Items

When configuring a user's data permissions for a certain business (usually configured on a role), the following four options are available:

1. `ONLY_SELF` - Can only view data belonging to oneself.
2. `SELF_DEPT` - Can view data of one's own department.
3. `SUB_DEPTS` - Can view data of one's own department and all sub - departments.
4. `ALL` - Can view all data.

## Code Implementation

### Custom Annotation

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DataScope {
    /**
     * Used to specify which table the data permission applies to (because it may be a joint table query).
     */
    String on() default "";
}
```

This annotation is used to mark which **retrieval entity classes** need to enable data permissions.

### Annotation Usage Instructions

```java
@SearchBean(
  tables = "order o, xxx x",     // Assume a joint table query is required.
  where = "o.xxx_id = x.id <ds>" // Placeholder <ds> is used to mark where the data permission condition needs to be inserted.
)
@DataScope(on = "o")             // Enable data permissions and apply them to the order table.
public class OrderVO {
    ...
}
```

Here is a trick: use a placeholder (`<ds>`) to mark where the data permission condition needs to be inserted. In this way, our custom code does not need to introduce a third - party framework to parse SQL syntax, so our code can be simpler.

### Data Permission Interceptor

Next, a custom data permission interceptor can be created:

```java
@Component
public class DataScopeSqlInterceptor implements SqlInterceptor {

    @Override
    public <T> SearchSql<T> intercept(SearchSql<T> searchSql, Map<String, Object> paraMap, FetchType fetchType) {
        // Get the @DataScope annotation.
        DataScope dataScope = searchSql.getBeanMeta().getBeanClass().getAnnotation(DataScope.class);
        if (dataScope == null) {
            // If data permissions are not enabled, return directly.
            return searchSql;
        }
        // Process the data permissions of the list query SQL.
        searchSql.setListSqlString(process(dataScope, searchSql.getListSqlString()));
        // Process the data permissions of the grouped table query SQL.
        searchSql.setClusterSqlString(process(dataScope, searchSql.getClusterSqlString()));
        return searchSql;
    }

    static final String PLACEHOLDER = "<ds>";   // Placeholder

    private String process(DataScope dataScope, String sql) {
        int index = sql.indexOf(PLACEHOLDER);
        if (index < 0) {
            // If there is no placeholder, return directly.
            return sql;
        }
        // SQL before the placeholder.
        String preSql = sql.substring(0, index);
        // SQL after the placeholder.
        String nexSql = sql.substring(index + PLACEHOLDER.length());
        // Get the information of the current requester (the methods used here vary in different project architectures).
        PrincipalUser user = PrincipalHolder.requireUser();
        // Data permission SQL condition.
        String scopeSql = buildScopeSql(dataScope.on(), user);
        // Concatenate the new complete SQL.
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
        // Get the data permission configuration type of the current user.
        ScopeType scopeType = user.getScopeType();
        // Query only data belonging to oneself.
        if (scopeType == ScopeType.ONLY_SELF) {
            return ownerId(on) + " = " + user.getId();
        }
        // Query only data of one's own department.
        if (scopeType == ScopeType.SELF_DEPT) {
            return deptId(on) + " = " + user.getDeptId();
        }
        // Query data of one's own department and all sub - departments.
        if (scopeType == ScopeType.SUB_DEPTS) {
            // Recursive SQL syntax is used here. If the database engine does not support this syntax,
            // all sub - department IDs can also be queried separately first and then concatenated here.
            return deptId(on) + "in (with recursive d_tree as (" +
                " select id, parent_id from dept" +
                "  where id = " + user.getDeptId() +
                " union all" +
                " select d.id, d.parent_id from dept d, d_tree t" +
                "  where t.id = d.parent_id" +
                ") select id from d_tree)"; 
        }
        // Query all data.
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

The above implements a data permission mechanism. Other types of data permissions can be implemented in the same way.
