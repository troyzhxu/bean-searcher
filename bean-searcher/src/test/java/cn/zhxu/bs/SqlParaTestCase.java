package cn.zhxu.bs;

import cn.zhxu.bs.bean.SearchBean;
import cn.zhxu.bs.util.MapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class SqlParaTestCase {

    @SearchBean(where = "id in (:idList:) and age = :age")
    public static class User1 {
        private String name;
    }

    @Test
    public void test_01() {
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
                .sqlExecutor(new SqlExecutor() {
                    @Override
                    public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                        Assertions.assertEquals("select name c_0 from user1 where (id in (1,2,3) and age = ?)", searchSql.getListSqlString());
                        List<Object> params = searchSql.getListSqlParams();
                        Assertions.assertEquals(1, params.size());
                        Assertions.assertEquals(20, params.get(0));
                        return new SqlResult<>(searchSql);
                    }
                })
                .build();
        mapSearcher.searchAll(User1.class, MapUtils.builder()
                .put("idList", Arrays.asList(1, 2, 3))
                .put("age", 20)
                .build());
        mapSearcher.searchAll(User1.class, MapUtils.builder()
                .put("idList", new int[]{1, 2, 3})
                .put("age", 20)
                .build());
        mapSearcher.searchAll(User1.class, MapUtils.builder()
                .put("idList", new Integer[]{1, 2, 3})
                .put("age", 20)
                .build());
        mapSearcher.searchAll(User1.class, MapUtils.builder()
                .put("idList", new Object[]{1, 2, 3})
                .put("age", 20)
                .build());
        System.out.println("\ttest_01 ok!");
    }

    @SearchBean(where = "age = :age and status in (:statues:)")
    public static class User2 {
        private String name;
    }

    @Test
    public void test_02() {
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
                .sqlExecutor(new SqlExecutor() {
                    @Override
                    public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                        Assertions.assertEquals("select name c_0 from user2 where (age = ? and status in ('A','B','C'))", searchSql.getListSqlString());
                        List<Object> params = searchSql.getListSqlParams();
                        Assertions.assertEquals(1, params.size());
                        Assertions.assertEquals(20, params.get(0));
                        return new SqlResult<>(searchSql);
                    }
                })
                .build();
        mapSearcher.searchAll(User2.class, MapUtils.builder()
                .put("statues", Arrays.asList("A", "B", "C"))
                .put("age", 20)
                .build());
        mapSearcher.searchAll(User2.class, MapUtils.builder()
                .put("statues", new String[]{"A", "B", "C"})
                .put("age", 20)
                .build());
        mapSearcher.searchAll(User2.class, MapUtils.builder()
                .put("statues", new Object[]{"A", "B", "C"})
                .put("age", 20)
                .build());
        System.out.println("\ttest_01 ok!");
    }

}
