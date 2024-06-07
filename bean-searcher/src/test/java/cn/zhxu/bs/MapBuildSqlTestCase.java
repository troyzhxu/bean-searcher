package cn.zhxu.bs;

import cn.zhxu.bs.util.MapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MapBuildSqlTestCase {

    public static class User {
        private long id;
        private String name;
        private int age;
        public long getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public int getAge() {
            return age;
        }
    }

    @Test
    public void test_01() {
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
                .sqlExecutor(new SqlExecutor() {
                    @Override
                    public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                        Assertions.assertEquals("select id c_0, name c_1, age c_2 from user where (10 < id and name = 'jack')", searchSql.getListSqlString());
                        return new SqlResult<>(searchSql);
                    }
                })
                .build();
        Map<String, Object> params = MapUtils.builder()
                .field(User::getId, User::getName).sql("10 < $1 and $2 = 'jack'")
                .build();
        mapSearcher.searchAll(User.class, params);
        System.out.println("\ttest_01 ok!");
    }

    @Test
    public void test_02() {
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
                .sqlExecutor(new SqlExecutor() {
                    @Override
                    public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                        Assertions.assertEquals("select id c_0, name c_1, age c_2 from user where (? < id and name = ?)", searchSql.getListSqlString());
                        Assertions.assertArrayEquals(new Object[] {15, "jack"}, searchSql.getListSqlParams().toArray(new Object[0]));
                        return new SqlResult<>(searchSql);
                    }
                })
                .build();
        Map<String, Object> params = MapUtils.builder()
                .field(User::getId, User::getName).sql("? < $1 and $2 = ?", 15, "jack")
                .build();
        mapSearcher.searchAll(User.class, params);
        System.out.println("\ttest_02 ok!");
    }

    @Test
    public void test_03() {
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
                .sqlExecutor(new SqlExecutor() {
                    @Override
                    public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                        Assertions.assertEquals("select id c_0, name c_1, age c_2 from user where (age = ? and 10 = id)", searchSql.getListSqlString());
                        Assertions.assertArrayEquals(new Object[] {15}, searchSql.getListSqlParams().toArray(new Object[0]));
                        return new SqlResult<>(searchSql);
                    }
                })
                .build();
        Map<String, Object> params = MapUtils.builder()
                .field(User::getId, User::getAge).sql("$2 = ? and 10 = $1", 15)
                .build();
        mapSearcher.searchAll(User.class, params);
        System.out.println("\ttest_03 ok!");
    }

}
