package cn.zhxu.bs;

import cn.zhxu.bs.bean.DbField;
import cn.zhxu.bs.bean.DbIgnore;
import cn.zhxu.bs.bean.SearchBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 针对 Java record 类的端到端集成测试：验证 SQL 生成与结果映射均正确。
 * <p>
 * 说明：
 * - record 字段使用包装类型（Long、Integer），避免 mock 数据装箱兼容性问题。
 * - MapUtils.builder().field(record::accessor) 不可用，因为 FieldFns 只处理 getXxx/isXxx 形式的
 *   getter，而 record 的 accessor 方法名与字段名相同（无前缀），测试中改用字符串 key 传参。
 * @since v4.9.0
 */
public class RecordSearchTestCase {

    // -----------------------------------------------------------------------
    // 被测 SearchBean record
    // -----------------------------------------------------------------------

    /** 最简单的 record，表名与列名均由框架自动推断 */
    public record UserRecord(Long id, String name, Integer age) {}

    /** 带 @SearchBean 与 @DbField 注解的多表 record */
    @SearchBean(tables = "user u, role r", where = "u.role_id = r.id", autoMapTo = "u")
    public record UserRoleRecord(
            Long id,
            String name,
            @DbField("r.name") String roleName
    ) {}

    /** 含 @DbIgnore 的 record */
    public record UserWithIgnore(Long id, String name, @DbIgnore String memo) {}

    // -----------------------------------------------------------------------
    // 工具方法
    // -----------------------------------------------------------------------

    /**
     * 构造一个 mock SqlExecutor：
     *   - 列表查询返回 rows 中的数据（每个 Map 为一行，key 为列别名）
     *   - 聚合查询返回 count
     */
    private SqlExecutor mockExecutor(List<Map<String, Object>> rows, long count) {
        return new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                SqlResult.ResultSet listRs = new SqlResult.ResultSet() {
                    int cursor = -1;
                    @Override
                    public boolean next() { return ++cursor < rows.size(); }
                    @Override
                    public Object get(String columnLabel) { return rows.get(cursor).get(columnLabel); }
                };
                SqlResult.Result clusterRs = columnLabel -> count;
                return new SqlResult<>(searchSql, listRs, clusterRs);
            }
        };
    }

    // -----------------------------------------------------------------------
    // test_r01：基础 record 的 SQL 生成验证
    // -----------------------------------------------------------------------

    @Test
    public void test_r01_sql_generation() {
        AtomicReference<String> capturedSql = new AtomicReference<>();
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                capturedSql.set(searchSql.getListSqlString());
                return new SqlResult<>(searchSql, SqlResult.ResultSet.EMPTY, col -> 0L);
            }
        };

        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();
        beanSearcher.search(UserRecord.class);

        String sql = capturedSql.get();
        Assertions.assertNotNull(sql);
        Assertions.assertTrue(sql.contains("id"),   "SQL 应包含 id 字段");
        Assertions.assertTrue(sql.contains("name"), "SQL 应包含 name 字段");
        Assertions.assertTrue(sql.contains("age"),  "SQL 应包含 age 字段");
        Assertions.assertTrue(sql.contains("user_record"), "SQL 应包含自动推断的表名 user_record");
        System.out.println("\ttest_r01_sql_generation ok! SQL: " + sql);
    }

    // -----------------------------------------------------------------------
    // test_r02：基础 record 结果映射
    // -----------------------------------------------------------------------

    @Test
    public void test_r02_result_mapping() {
        MetaResolver metaResolver = new cn.zhxu.bs.implement.DefaultMetaResolver();
        BeanMeta<UserRecord> meta = metaResolver.resolve(UserRecord.class);

        Map<String, Object> row = buildUserRow(meta, 10L, "Alice", 25);

        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
                .sqlExecutor(mockExecutor(List.of(row), 1L))
                .build();

        List<UserRecord> results = beanSearcher.searchAll(UserRecord.class);
        Assertions.assertEquals(1, results.size());

        UserRecord user = results.get(0);
        Assertions.assertEquals(10L,     user.id());
        Assertions.assertEquals("Alice", user.name());
        Assertions.assertEquals(25,      user.age());
        System.out.println("\ttest_r02_result_mapping ok!");
    }

    // -----------------------------------------------------------------------
    // test_r03：多行结果映射
    // -----------------------------------------------------------------------

    @Test
    public void test_r03_multiple_rows() {
        MetaResolver metaResolver = new cn.zhxu.bs.implement.DefaultMetaResolver();
        BeanMeta<UserRecord> meta = metaResolver.resolve(UserRecord.class);

        List<Map<String, Object>> rows = List.of(
                buildUserRow(meta, 1L, "Alice", 20),
                buildUserRow(meta, 2L, "Bob",   30),
                buildUserRow(meta, 3L, "Carol", 25)
        );

        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
                .sqlExecutor(mockExecutor(rows, 3L))
                .build();

        List<UserRecord> results = beanSearcher.searchAll(UserRecord.class);
        Assertions.assertEquals(3, results.size());
        Assertions.assertEquals(1L,      results.get(0).id());
        Assertions.assertEquals("Bob",   results.get(1).name());
        Assertions.assertEquals(25,      results.get(2).age());
        System.out.println("\ttest_r03_multiple_rows ok!");
    }

    // -----------------------------------------------------------------------
    // test_r04：@DbIgnore 的组件不出现在 SQL 中，且映射结果中该字段为 null
    // -----------------------------------------------------------------------

    @Test
    public void test_r04_db_ignore() {
        AtomicReference<String> capturedSql = new AtomicReference<>();

        MetaResolver metaResolver = new cn.zhxu.bs.implement.DefaultMetaResolver();
        BeanMeta<UserWithIgnore> meta = metaResolver.resolve(UserWithIgnore.class);

        Map<String, Object> row = new HashMap<>();
        meta.getFieldMetas().forEach(f -> {
            if ("id".equals(f.getName()))   row.put(f.getDbAlias(), 42L);
            if ("name".equals(f.getName())) row.put(f.getDbAlias(), "Dave");
        });

        SqlExecutor executor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                capturedSql.set(searchSql.getListSqlString());
                return new SqlResult<>(searchSql,
                        new SqlResult.ResultSet() {
                            int cursor = -1;
                            @Override public boolean next() { return ++cursor < 1; }
                            @Override public Object get(String col) { return row.get(col); }
                        },
                        col -> 1L);
            }
        };

        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(executor).build();
        List<UserWithIgnore> results = beanSearcher.searchAll(UserWithIgnore.class);

        // SQL 中不应出现 memo
        Assertions.assertFalse(capturedSql.get().contains("memo"), "SQL 不应含被 @DbIgnore 的字段 memo");

        Assertions.assertEquals(1, results.size());
        UserWithIgnore u = results.get(0);
        Assertions.assertEquals(42L,    u.id());
        Assertions.assertEquals("Dave", u.name());
        Assertions.assertNull(u.memo(), "被 @DbIgnore 的 memo 应为 null");
        System.out.println("\ttest_r04_db_ignore ok! SQL: " + capturedSql.get());
    }

    // -----------------------------------------------------------------------
    // test_r05：多表 record (@SearchBean + @DbField)
    // -----------------------------------------------------------------------

    @Test
    public void test_r05_multi_table() {
        AtomicReference<String> capturedSql = new AtomicReference<>();

        MetaResolver metaResolver = new cn.zhxu.bs.implement.DefaultMetaResolver();
        BeanMeta<UserRoleRecord> meta = metaResolver.resolve(UserRoleRecord.class);

        Map<String, Object> row = new HashMap<>();
        meta.getFieldMetas().forEach(f -> {
            switch (f.getName()) {
                case "id"       -> row.put(f.getDbAlias(), 7L);
                case "name"     -> row.put(f.getDbAlias(), "Eve");
                case "roleName" -> row.put(f.getDbAlias(), "Admin");
            }
        });

        SqlExecutor executor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                capturedSql.set(searchSql.getListSqlString());
                return new SqlResult<>(searchSql,
                        new SqlResult.ResultSet() {
                            int cursor = -1;
                            @Override public boolean next() { return ++cursor < 1; }
                            @Override public Object get(String col) { return row.get(col); }
                        },
                        col -> 1L);
            }
        };

        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(executor).build();
        List<UserRoleRecord> results = beanSearcher.searchAll(UserRoleRecord.class);

        String sql = capturedSql.get();
        Assertions.assertTrue(sql.contains("user u, role r"),  "SQL 应包含多表声明");
        Assertions.assertTrue(sql.contains("r.name"),          "SQL 应包含 r.name");
        Assertions.assertTrue(sql.contains("u.role_id = r.id"), "SQL 应包含 where 条件");

        Assertions.assertEquals(1, results.size());
        UserRoleRecord r = results.get(0);
        Assertions.assertEquals(7L,       r.id());
        Assertions.assertEquals("Eve",    r.name());
        Assertions.assertEquals("Admin",  r.roleName());
        System.out.println("\ttest_r05_multi_table ok! SQL: " + sql);
    }

    // -----------------------------------------------------------------------
    // test_r06：record 作为 SearchBean 进行带条件的检索（使用字符串 key 传参）
    // 注：MapUtils.builder().field(record::accessor) 目前不支持 record accessor 方法引用，
    //     因为 FieldFns 只处理 getXxx/isXxx 形式，record accessor 方法名与字段名相同
    // -----------------------------------------------------------------------

    @Test
    public void test_r06_search_with_param() {
        AtomicReference<String> capturedSql = new AtomicReference<>();
        AtomicReference<List<Object>> capturedParams = new AtomicReference<>();

        SqlExecutor executor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                capturedSql.set(searchSql.getListSqlString());
                capturedParams.set(searchSql.getListSqlParams());
                return new SqlResult<>(searchSql, SqlResult.ResultSet.EMPTY, col -> 0L);
            }
        };

        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(executor).build();

        // 使用字符串参数 key
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Alice");
        beanSearcher.searchAll(UserRecord.class, params);

        String sql = capturedSql.get();
        List<Object> sqlParams = capturedParams.get();

        Assertions.assertTrue(sql.contains("name =") || sql.contains("name="), "SQL 应包含 name 等值条件");
        Assertions.assertTrue(sqlParams.contains("Alice"), "SQL 参数应含 Alice");
        System.out.println("\ttest_r06_search_with_param ok! SQL: " + sql);
    }

    // -----------------------------------------------------------------------
    // test_r07：MapSearcher 对 record 类的 SQL 生成与结果映射
    // -----------------------------------------------------------------------

    @Test
    public void test_r07_map_searcher() {
        MetaResolver metaResolver = new cn.zhxu.bs.implement.DefaultMetaResolver();
        BeanMeta<UserRecord> meta = metaResolver.resolve(UserRecord.class);

        Map<String, Object> row = buildUserRow(meta, 1L, "Frank", 18);

        MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
                .sqlExecutor(mockExecutor(List.of(row), 1L))
                .build();

        List<Map<String, Object>> results = mapSearcher.searchAll(UserRecord.class);
        Assertions.assertEquals(1, results.size());
        Map<String, Object> result = results.get(0);
        Assertions.assertEquals(1L,      result.get("id"));
        Assertions.assertEquals("Frank", result.get("name"));
        Assertions.assertEquals(18,      result.get("age"));
        System.out.println("\ttest_r07_map_searcher ok!");
    }

    // -----------------------------------------------------------------------
    // 工具方法
    // -----------------------------------------------------------------------

    private Map<String, Object> buildUserRow(BeanMeta<UserRecord> meta, Long id, String name, Integer age) {
        Map<String, Object> row = new HashMap<>();
        meta.getFieldMetas().forEach(f -> {
            switch (f.getName()) {
                case "id"   -> row.put(f.getDbAlias(), id);
                case "name" -> row.put(f.getDbAlias(), name);
                case "age"  -> row.put(f.getDbAlias(), age);
            }
        });
        return row;
    }

}
