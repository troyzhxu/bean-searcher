package cn.zhxu.bs;

import cn.zhxu.bs.bean.SearchBean;
import cn.zhxu.bs.util.MapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class OrderByTestCase {

    public static class User {
        private Integer id;
        private String name;
        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }
    }

    @Test
    public void test_01() {
        doTest(User.class, "select id c_0, name c_1 from user", null);
        System.out.println("\ttest_01 ok!");
    }

    @Test
    public void test_02() {
        doTest(User.class, "select id c_0, name c_1 from user order by c_0 asc", MapUtils.builder().orderBy(User::getId).build());
        System.out.println("\ttest_02 ok!");
    }

    @Test
    public void test_03() {
        doTest(User.class, "select id c_0, name c_1 from user order by c_0 desc", MapUtils.builder().orderBy(User::getId).desc().build());
        System.out.println("\ttest_03 ok!");
    }

    @Test
    public void test_04() {
        doTest(User.class, "select id c_0, name c_1 from user order by c_0 desc, c_1 asc",
                MapUtils.builder()
                        .orderBy(User::getId).desc()
                        .orderBy(User::getName).asc()
                        .build()
        );
        System.out.println("\ttest_04 ok!");
    }

    @Test
    public void test_05() {
        doTest(User.class, "select id c_0, name c_1 from user order by c_0 desc",
                MapUtils.builder()
                        .put("sort", "id")
                        .put("order", "desc")
                        .build()
        );
        System.out.println("\ttest_05 ok!");
    }

    @Test
    public void test_06() {
        doTest(User.class, "select id c_0, name c_1 from user order by c_0 asc, c_1 desc",
                MapUtils.builder()
                        .put("orderBy", "id:asc,name:desc")
                        .build()
        );
        System.out.println("\ttest_06 ok!");
    }

    @SearchBean(tables = "user", orderBy = "time desc")
    public static class SubUser1 extends User { }

    @Test
    public void test_11() {
        doTest(SubUser1.class, "select id c_0, name c_1 from user order by time desc", null);
        System.out.println("\ttest_11 ok!");
    }

    @Test
    public void test_12() {
        doTest(SubUser1.class, "select id c_0, name c_1 from user order by c_0 asc", MapUtils.builder().orderBy(User::getId).build());
        System.out.println("\ttest_12 ok!");
    }

    @Test
    public void test_13() {
        doTest(SubUser1.class, "select id c_0, name c_1 from user order by c_0 desc", MapUtils.builder().orderBy(User::getId).desc().build());
        System.out.println("\ttest_13 ok!");
    }

    @Test
    public void test_14() {
        doTest(SubUser1.class, "select id c_0, name c_1 from user order by c_0 desc, c_1 asc",
                MapUtils.builder()
                        .orderBy(User::getId).desc()
                        .orderBy(User::getName).asc()
                        .build()
        );
        System.out.println("\ttest_14 ok!");
    }

    @Test
    public void test_15() {
        doTest(SubUser1.class, "select id c_0, name c_1 from user order by c_0 desc",
                MapUtils.builder()
                        .put("sort", "id")
                        .put("order", "desc")
                        .build()
        );
        System.out.println("\ttest_15 ok!");
    }

    @Test
    public void test_16() {
        doTest(SubUser1.class, "select id c_0, name c_1 from user order by c_0 asc, c_1 desc",
                MapUtils.builder()
                        .put("orderBy", "id:asc,name:desc")
                        .build()
        );
        System.out.println("\ttest_16 ok!");
    }


    @SearchBean(tables = "user", orderBy = ":myOrderBy:")
    public static class SubUser2 extends User { }

    @Test
    public void test_21_1() {
        doTest(SubUser2.class, "select id c_0, name c_1 from user", null);
        System.out.println("\ttest_21_1 ok!");
    }

    @Test
    public void test_21_2() {
        doTest(SubUser2.class, "select id c_0, name c_1 from user order by id asc", MapUtils.builder().put("myOrderBy", "id asc").build());
        System.out.println("\ttest_21_2 ok!");
    }

    @Test
    public void test_21_3() {
        doTest(SubUser2.class, "select id c_0, name c_1 from user order by id asc, name desc", MapUtils.builder().put("myOrderBy", "id asc, name desc").build());
        System.out.println("\ttest_21_3 ok!");
    }

    @Test
    public void test_22() {
        doTest(SubUser2.class, "select id c_0, name c_1 from user order by c_0 asc", MapUtils.builder().orderBy(User::getId).build());
        System.out.println("\ttest_22 ok!");
    }

    @Test
    public void test_23() {
        doTest(SubUser2.class, "select id c_0, name c_1 from user order by c_0 desc", MapUtils.builder().orderBy(User::getId).desc().build());
        System.out.println("\ttest_23 ok!");
    }

    @Test
    public void test_24() {
        doTest(SubUser2.class, "select id c_0, name c_1 from user order by c_0 desc, c_1 asc",
                MapUtils.builder()
                        .orderBy(User::getId).desc()
                        .orderBy(User::getName).asc()
                        .build()
        );
        System.out.println("\ttest_24 ok!");
    }

    @Test
    public void test_25() {
        doTest(SubUser2.class, "select id c_0, name c_1 from user order by c_0 desc",
                MapUtils.builder()
                        .put("sort", "id")
                        .put("order", "desc")
                        .build()
        );
        System.out.println("\ttest_25 ok!");
    }

    @Test
    public void test_26() {
        doTest(SubUser2.class, "select id c_0, name c_1 from user order by c_0 asc, c_1 desc",
                MapUtils.builder()
                        .put("orderBy", "id:asc,name:desc")
                        .build()
        );
        System.out.println("\ttest_26 ok!");
    }

    @SearchBean(tables = "user", orderBy = "id desc:extOrderBy:")
    public static class SubUser3 extends User { }

    @Test
    public void test_31_1() {
        doTest(SubUser3.class, "select id c_0, name c_1 from user order by id desc", null);
        System.out.println("\ttest_31_1 ok!");
    }

    @Test
    public void test_31_2() {
        doTest(SubUser3.class, "select id c_0, name c_1 from user order by id desc, name asc", MapUtils.builder().put("extOrderBy", ", name asc").build());
        System.out.println("\ttest_31_2 ok!");
    }

    @Test
    public void test_32() {
        doTest(SubUser3.class, "select id c_0, name c_1 from user order by c_0 asc", MapUtils.builder().orderBy(User::getId).build());
        System.out.println("\ttest_32 ok!");
    }

    private static <T extends User> void doTest(Class<T> beanClass, String sqlExpected, Map<String, Object> params) {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                String listSql = searchSql.getListSqlString();
                System.out.println(listSql);
                Assertions.assertEquals(sqlExpected, listSql);
                return new SqlResult<>(searchSql, new SqlResult.ResultSet() {
                    private boolean next = true;
                    @Override
                    public boolean next() {
                        if (next) {
                            next = false;
                            return true;
                        }
                        return false;
                    }
                    @Override
                    public Object get(String columnLabel) {
                        if ("c_0".equals(columnLabel)) {
                            return 100;
                        }
                        if ("c_1".equals(columnLabel)) {
                            return "Jack";
                        }
                        throw new RuntimeException();
                    }
                }, null);
            }
        };
        List<T> list = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build().searchAll(beanClass, params);
        Assertions.assertEquals(1, list.size());
        T user = list.get(0);
        Assertions.assertEquals(100, user.getId());
        Assertions.assertEquals("Jack", user.getName());
    }

}
