package com.ejlchina.searcher;

import com.ejlchina.searcher.util.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestCase1 {

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
    public void test1() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                Assert.assertEquals("select name c_1, id c_0 from search_bean limit ?, ?", searchSql.getListSqlString());
                List<Object> listParams = searchSql.getListSqlParams();
                Assert.assertEquals(2, listParams.size());
                Assert.assertEquals(0L, listParams.get(0));
                Assert.assertEquals(15, listParams.get(1));
                Assert.assertEquals("select count(*) s_count from search_bean", searchSql.getClusterSqlString());
                Assert.assertEquals(0, searchSql.getClusterSqlParams().size());
                Assert.assertEquals("s_count", searchSql.getCountAlias());
                Assert.assertEquals(0, searchSql.getSummaryAliases().size());
                return new SqlResult<>(searchSql);
            }
        };

        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        mapSearcher.search(SearchBean.class, new HashMap<>());
        mapSearcher.search(SearchBean.class, null);

        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();
        beanSearcher.search(SearchBean.class, new HashMap<>());
        beanSearcher.search(SearchBean.class, null);
    }

    @Test
    public void test2() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                System.out.println(searchSql.getListSqlString());
                System.out.println(searchSql.getListSqlParams());

                Assert.assertEquals("select name c_1, id c_0 from search_bean where (id = ?) limit ?, ?", searchSql.getListSqlString());
                List<Object> listParams = searchSql.getListSqlParams();
                Assert.assertEquals(3, listParams.size());
                Assert.assertEquals(1, listParams.get(0));
                Assert.assertEquals(0L, listParams.get(1));
                Assert.assertEquals(15, listParams.get(2));

                System.out.println(searchSql.getClusterSqlString());
                Assert.assertEquals("select count(*) s_count from search_bean where (id = ?)", searchSql.getClusterSqlString());

                List<Object> clusterSqlParams = searchSql.getClusterSqlParams();
                System.out.println(clusterSqlParams);
                Assert.assertEquals(1, clusterSqlParams.size());
                Assert.assertEquals(1, clusterSqlParams.get(0));

                System.out.println(searchSql.getCountAlias());

                Assert.assertEquals("s_count", searchSql.getCountAlias());

                System.out.println(searchSql.getSummaryAliases());

                Assert.assertEquals(0, searchSql.getSummaryAliases().size());
                return new SqlResult<>(searchSql);
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
    }


}
