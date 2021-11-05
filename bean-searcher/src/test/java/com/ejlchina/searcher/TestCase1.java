package com.ejlchina.searcher;

import com.ejlchina.searcher.util.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCase1 {

    public static class Bean {
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
    public void test1() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                Assert.assertEquals("select name _1, id _0 from bean limit ?, ?", searchSql.getListSqlString());
                List<Object> listParams = searchSql.getListSqlParams();
                Assert.assertEquals(2, listParams.size());
                Assert.assertEquals(0L, listParams.get(0));
                Assert.assertEquals(15, listParams.get(1));
                Assert.assertEquals("select count(*) _count from bean", searchSql.getClusterSqlString());
                Assert.assertEquals(0, searchSql.getClusterSqlParams().size());
                Assert.assertEquals("_count", searchSql.getCountAlias());
                Assert.assertEquals(0, searchSql.getSummaryAliases().size());
                return new SqlResult<>(searchSql);
            }
        };

        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        mapSearcher.search(Bean.class, new HashMap<>());
        mapSearcher.search(Bean.class, null);

        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();
        beanSearcher.search(Bean.class, new HashMap<>());
        beanSearcher.search(Bean.class, null);
    }

    @Test
    public void test2() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                System.out.println(searchSql.getListSqlString());
                System.out.println(searchSql.getListSqlParams());

                Assert.assertEquals("select name _1, id _0 from bean where (id = ?) limit ?, ?", searchSql.getListSqlString());
                List<Object> listParams = searchSql.getListSqlParams();
                Assert.assertEquals(3, listParams.size());
                Assert.assertEquals(1, listParams.get(0));
                Assert.assertEquals(0L, listParams.get(1));
                Assert.assertEquals(15, listParams.get(2));

                System.out.println(searchSql.getClusterSqlString());
                Assert.assertEquals("select count(*) _count from bean where (id = ?)", searchSql.getClusterSqlString());

                List<Object> clusterSqlParams = searchSql.getClusterSqlParams();
                System.out.println(clusterSqlParams);
                Assert.assertEquals(1, clusterSqlParams.size());
                Assert.assertEquals(1, clusterSqlParams.get(0));

                System.out.println(searchSql.getCountAlias());

                Assert.assertEquals("_count", searchSql.getCountAlias());

                System.out.println(searchSql.getSummaryAliases());

                Assert.assertEquals(0, searchSql.getSummaryAliases().size());
                return new SqlResult<>(searchSql);
            }
        };
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();

        Map<String, Object> params1 = new HashMap<>();
        params1.put("id", 1);
        Map<String, Object> params2 = MapUtils.builder().field(Bean::getId, 1).build();

        mapSearcher.search(Bean.class, params1);
        mapSearcher.search(Bean.class, params2);

        beanSearcher.search(Bean.class, params1);
        beanSearcher.search(Bean.class, params2);
    }


}
