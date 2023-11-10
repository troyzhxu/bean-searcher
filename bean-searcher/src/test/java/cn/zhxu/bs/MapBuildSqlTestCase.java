package cn.zhxu.bs;

import cn.zhxu.bs.util.MapUtils;
import org.junit.Assert;
import org.junit.Test;

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


    public record UserRecord(
            long id,
            String name,
            int age
    ) {
    }

    @Test
    public void test_01() {
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
                .sqlExecutor(new SqlExecutor() {
                    @Override
                    public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                        Assert.assertEquals("select id c_0, name c_1, age c_2 from user where (10 < id and name = 'jack')", searchSql.getListSqlString());
                        return new SqlResult<>(searchSql);
                    }
                })
                .build();
        Map<String, Object> params = MapUtils.builder()
                .field(User::getId, User::getName).sql("10 < $1 and $2 = 'jack'")
                .build();
        mapSearcher.searchAll(User.class, params);
    }

    @Test
    public void test_02() {
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
                .sqlExecutor(new SqlExecutor() {
                    @Override
                    public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                        Assert.assertEquals("select id c_0, name c_1, age c_2 from user where (? < id and name = ?)", searchSql.getListSqlString());
                        Assert.assertArrayEquals(new Object[]{15, "jack"}, searchSql.getListSqlParams().toArray(new Object[0]));
                        return new SqlResult<>(searchSql);
                    }
                })
                .build();
        Map<String, Object> params = MapUtils.builder()
                .field(User::getId, User::getName).sql("? < $1 and $2 = ?", 15, "jack")
                .build();
        mapSearcher.searchAll(User.class, params);
    }

    @Test
    public void test_03() {
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
                .sqlExecutor(new SqlExecutor() {
                    @Override
                    public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                        Assert.assertEquals("select id c_0, name c_1, age c_2 from user where (age = ? and 10 = id)", searchSql.getListSqlString());
                        Assert.assertArrayEquals(new Object[]{15}, searchSql.getListSqlParams().toArray(new Object[0]));
                        return new SqlResult<>(searchSql);
                    }
                })
                .build();
        Map<String, Object> params = MapUtils.builder()
                .field(User::getId, User::getAge).sql("$2 = ? and 10 = $1", 15)
                .build();
        mapSearcher.searchAll(User.class, params);
    }


    @Test
    public void test_record_01() {
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
                .sqlExecutor(new SqlExecutor() {
                    @Override
                    public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                        Assert.assertEquals("select id c_0, name c_1, age c_2 from user_record where (10 < id and name = 'jack')", searchSql.getListSqlString());
                        return new SqlResult<>(searchSql);
                    }
                })
                .build();
        Map<String, Object> params = MapUtils.builder()
                .field(UserRecord::id, UserRecord::name).sql("10 < $1 and $2 = 'jack'")
                .build();
        mapSearcher.searchAll(UserRecord.class, params);
    }

    @Test
    public void test_record_02() {
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
                .sqlExecutor(new SqlExecutor() {
                    @Override
                    public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                        Assert.assertEquals("select id c_0, name c_1, age c_2 from user_record where (? < id and name = ?)", searchSql.getListSqlString());
                        Assert.assertArrayEquals(new Object[]{15, "jack"}, searchSql.getListSqlParams().toArray(new Object[0]));
                        return new SqlResult<>(searchSql);
                    }
                })
                .build();
        Map<String, Object> params = MapUtils.builder()
                .field(UserRecord::id, UserRecord::name).sql("? < $1 and $2 = ?", 15, "jack")
                .build();
        mapSearcher.searchAll(UserRecord.class, params);
    }

    @Test
    public void test_record_03() {
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
                .sqlExecutor(new SqlExecutor() {
                    @Override
                    public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                        Assert.assertEquals("select id c_0, name c_1, age c_2 from user_record where (age = ? and 10 = id)", searchSql.getListSqlString());
                        Assert.assertArrayEquals(new Object[]{15}, searchSql.getListSqlParams().toArray(new Object[0]));
                        return new SqlResult<>(searchSql);
                    }
                })
                .build();
        Map<String, Object> params = MapUtils.builder()
                .field(UserRecord::id, UserRecord::age).sql("$2 = ? and 10 = $1", 15)
                .build();
        mapSearcher.searchAll(UserRecord.class, params);
    }
}
