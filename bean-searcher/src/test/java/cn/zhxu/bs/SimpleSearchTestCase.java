package cn.zhxu.bs;

import cn.zhxu.bs.implement.DefaultParamResolver;
import cn.zhxu.bs.operator.IsNull;
import cn.zhxu.bs.util.MapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleSearchTestCase {

    public static final SqlResult.ResultSet EMPTY_RESULT_SET = new SqlResult.ResultSet() {
        @Override
        public boolean next() {
            return false;
        }

        @Override
        public Object get(String columnLabel) {
            return null;
        }
    };

    public static class SearchBean {
        private long id;
        private String name;
        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void test_1() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                Assertions.assertEquals("select id c_0, name c_1 from search_bean limit ?, ?", searchSql.getListSqlString());
                List<Object> listParams = searchSql.getListSqlParams();
                Assertions.assertEquals(2, listParams.size());
                Assertions.assertEquals(0L, listParams.get(0));
                Assertions.assertEquals(15, listParams.get(1));
                Assertions.assertEquals("select count(*) s_count from search_bean", searchSql.getClusterSqlString());
                Assertions.assertEquals(0, searchSql.getClusterSqlParams().size());
                Assertions.assertEquals("s_count", searchSql.getCountAlias());
                Assertions.assertEquals(0, searchSql.getSummaryAliases().size());
                return new SqlResult<>(searchSql, EMPTY_RESULT_SET, columnLabel -> null);
            }
        };

        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        mapSearcher.search(SearchBean.class, new HashMap<>());
        mapSearcher.search(SearchBean.class);

        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();
        beanSearcher.search(SearchBean.class, new HashMap<>());
        beanSearcher.search(SearchBean.class);
        System.out.println("\ttest_1 ok!");
    }

    @Test
    public void test_2() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                System.out.println(searchSql.getListSqlString());
                System.out.println(searchSql.getListSqlParams());

                Assertions.assertEquals("select id c_0, name c_1 from search_bean where (id = ?) limit ?, ?", searchSql.getListSqlString());
                List<Object> listParams = searchSql.getListSqlParams();
                Assertions.assertEquals(3, listParams.size());
                Assertions.assertEquals(1L, listParams.get(0));
                Assertions.assertEquals(0L, listParams.get(1));
                Assertions.assertEquals(15, listParams.get(2));

                System.out.println(searchSql.getClusterSqlString());
                Assertions.assertEquals("select count(*) s_count from search_bean where (id = ?)", searchSql.getClusterSqlString());

                List<Object> clusterSqlParams = searchSql.getClusterSqlParams();
                System.out.println(clusterSqlParams);
                Assertions.assertEquals(1, clusterSqlParams.size());
                Assertions.assertEquals(1L, clusterSqlParams.get(0));

                System.out.println(searchSql.getCountAlias());

                Assertions.assertEquals("s_count", searchSql.getCountAlias());

                System.out.println(searchSql.getSummaryAliases());

                Assertions.assertEquals(0, searchSql.getSummaryAliases().size());
                return new SqlResult<>(searchSql, EMPTY_RESULT_SET, columnLabel -> null);
            }
        };
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();

        Map<String, Object> params1 = new HashMap<>();
        params1.put("id", 1);
        Map<String, Object> params2 = MapUtils.builder().field(SearchBean::getId, 1).build();

        mapSearcher.search(SearchBean.class, params1);
        mapSearcher.search(SearchBean.class, params2);

        beanSearcher.search(SearchBean.class, params1);
        beanSearcher.search(SearchBean.class, params2);
        System.out.println("\ttest_2 ok!");
    }

    @Test
    public void test_3() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                System.out.println(searchSql.getListSqlString());
                Assertions.assertEquals("select id c_0, name c_1 from search_bean order by c_1 asc", searchSql.getListSqlString());
                List<Object> listParams = searchSql.getListSqlParams();
                Assertions.assertEquals(0, listParams.size());
                Assertions.assertNull(searchSql.getClusterSqlString());
                List<Object> clusterSqlParams = searchSql.getClusterSqlParams();
                Assertions.assertEquals(0, clusterSqlParams.size());
                Assertions.assertNull(searchSql.getCountAlias());
                Assertions.assertEquals(0, searchSql.getSummaryAliases().size());
                return new SqlResult<>(searchSql, EMPTY_RESULT_SET, columnLabel -> null);
            }
        };
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();

        Map<String, Object> params1 = new HashMap<>();
        params1.put("sort", "name");
        params1.put("order", "asc");
        Map<String, Object> params2 = MapUtils.builder()
                .orderBy(SearchBean::getName, "asc")
                .build();
        mapSearcher.searchAll(SearchBean.class, params1);
        mapSearcher.searchAll(SearchBean.class, params2);
        beanSearcher.searchAll(SearchBean.class, params1);
        beanSearcher.searchAll(SearchBean.class, params2);
        System.out.println("\ttest_3 ok!");
    }

    @Test
    public void test_4() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                System.out.println(searchSql.getListSqlString());
                Assertions.assertEquals("select id c_0, name c_1 from search_bean where (((id = ?) or (id is null)) and (name = ?))", searchSql.getListSqlString());
                List<Object> listParams = searchSql.getListSqlParams();
                Assertions.assertEquals(2, listParams.size());
                Assertions.assertEquals(10L, listParams.get(0));
                Assertions.assertEquals("Jack", listParams.get(1));
                return new SqlResult<>(searchSql, EMPTY_RESULT_SET, columnLabel -> null);
            }
        };
        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();

        Map<String, Object> params = MapUtils.builder()
                .group("A")
                .field(SearchBean::getId, (Object) null)
                .group("B")
                .field(SearchBean::getId, 10)
                .group("C")
                .field(SearchBean::getId).op(IsNull.class)
                .group("D")
                .field(SearchBean::getName, "Jack")
                .groupExpr("(A|B|C)&D")
                .build();
        beanSearcher.searchAll(SearchBean.class, params);
        System.out.println("\ttest_4 ok!");
    }

    @Test
    public void test_5() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                System.out.println(searchSql.getListSqlString());
                Assertions.assertEquals("select id c_0, name c_1 from search_bean where ((name = ?) and ((id = ?) or (id is null)))", searchSql.getListSqlString());
                List<Object> listParams = searchSql.getListSqlParams();
                Assertions.assertEquals(2, listParams.size());
                Assertions.assertEquals("Jack", listParams.get(0));
                Assertions.assertEquals(10L, listParams.get(1));
                return new SqlResult<>(searchSql, EMPTY_RESULT_SET, columnLabel -> null);
            }
        };
        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();

        Map<String, Object> params = MapUtils.builder()
                .field(SearchBean::getName, "Jack")
                .or(o -> {
                    o.field(SearchBean::getId, (Object) null);
                    o.field(SearchBean::getId, 10);
                    o.field(SearchBean::getId).op(IsNull.class);
                })
                .build();
        beanSearcher.searchAll(SearchBean.class, params);

        params = MapUtils.builder()
                .or(o -> {
                    o.field(SearchBean::getId, (Object) null);
                    o.field(SearchBean::getId, 10);
                    o.field(SearchBean::getId).op(IsNull.class);
                })
                .field(SearchBean::getName, "Jack")
                .build();
        beanSearcher.searchAll(SearchBean.class, params);
        System.out.println("\ttest_5 ok!");
    }

    @Test
    public void test_6() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                System.out.println(searchSql.getListSqlString());
                Assertions.assertEquals("select id c_0, name c_1 from search_bean where (" +
                        "((id = ?) or (id is null))" +
                        " and " +
                        "((name like ?) and (id = ?) or (id = ?))" +
                        " and " +
                        "(name = ?)" +
                        ")", searchSql.getListSqlString());
                List<Object> listParams = searchSql.getListSqlParams();
                Assertions.assertEquals(5, listParams.size());
                Assertions.assertEquals(10L, listParams.get(0));
                Assertions.assertEquals("Tom%", listParams.get(1));
                Assertions.assertEquals(20L, listParams.get(2));
                Assertions.assertEquals(30L, listParams.get(3));
                Assertions.assertEquals("Jack", listParams.get(4));
                return new SqlResult<>(searchSql, EMPTY_RESULT_SET, columnLabel -> null);
            }
        };
        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();

        Map<String, Object> params = MapUtils.builder()
                .put("A.id", 20)
                .put("A.name", "Tom")
                .put("A.name-op", "sw")
                .put("B.id", 30)
                .put("gexpr", "A|B")
                .field(SearchBean::getName, "Jack")
                .or(o -> {
                    o.field(SearchBean::getId, 10);
                    o.field(SearchBean::getId).op(IsNull.class);
                })
                .build();
        beanSearcher.searchAll(SearchBean.class, params);
        System.out.println("\ttest_6 ok!");
    }

    @Test
    public void test_7() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                System.out.println(searchSql.getListSqlString());
                Assertions.assertEquals("select id c_0, name c_1 from search_bean where " +
                        "((name = ?) and ((id = ?) or (id is null)))", searchSql.getListSqlString());
                List<Object> listParams = searchSql.getListSqlParams();
                Assertions.assertEquals(2, listParams.size());
                Assertions.assertEquals("Jack", listParams.get(0));
                Assertions.assertEquals(10L, listParams.get(1));
                return new SqlResult<>(searchSql, EMPTY_RESULT_SET, columnLabel -> null);
            }
        };
        DefaultParamResolver paramResolver = new DefaultParamResolver();
        paramResolver.getConfiguration().gexprMerge(false);

        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
                .paramResolver(paramResolver)
                .sqlExecutor(sqlExecutor)
                .build();

        Map<String, Object> params = MapUtils.builder()
                .put("A.id", 20)
                .put("A.name", "Tom")
                .put("A.name-op", "sw")
                .put("B.id", 30)
                .put("gexpr", "A|B")
                .field(SearchBean::getName, "Jack")
                .or(o -> {
                    o.field(SearchBean::getId, 10);
                    o.field(SearchBean::getId).op(IsNull.class);
                })
                .build();
        beanSearcher.searchAll(SearchBean.class, params);
        System.out.println("\ttest_7 ok!");
    }

}
