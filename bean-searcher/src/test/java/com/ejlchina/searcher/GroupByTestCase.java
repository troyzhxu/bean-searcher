package com.ejlchina.searcher;

import com.ejlchina.searcher.bean.DbField;
import com.ejlchina.searcher.bean.SearchBean;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class GroupByTestCase {

    @SearchBean(groupBy = "course_id")
    public static class StudentCourse {
        private long courseId;
        @DbField("sum(score)")
        private int sumScore;
        @DbField("avg(score)")
        private int avgScore;
        public long getCourseId() {
            return courseId;
        }
        public int getSumScore() {
            return sumScore;
        }
        public int getAvgScore() {
            return avgScore;
        }
    }

    @Test
    public void test_count() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                System.out.println(searchSql.getClusterSqlString());
                Assert.assertNull(searchSql.getListSqlString());
                Assert.assertTrue(searchSql.getListSqlParams().isEmpty());
                Assert.assertEquals("select count(*) s_count from (select avg(score) c_2, sum(score) c_1, course_id c_0 from student_course group by course_id) t_", searchSql.getClusterSqlString());
                Assert.assertTrue(searchSql.getClusterSqlParams().isEmpty());
                return new SqlResult<>(searchSql, null, columnLabel -> 100);
            }
        };

        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        Assert.assertEquals(100, mapSearcher.searchCount(StudentCourse.class, new HashMap<>()));
        Assert.assertEquals(100, mapSearcher.searchCount(StudentCourse.class, null));

        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();
        Assert.assertEquals(100, beanSearcher.searchCount(StudentCourse.class, new HashMap<>()));
        Assert.assertEquals(100, beanSearcher.searchCount(StudentCourse.class, null));
    }

    @Test
    public void test_sum() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                Assert.assertNull(searchSql.getListSqlString());
                Assert.assertTrue(searchSql.getListSqlParams().isEmpty());
                Assert.assertEquals("select sum(c_1) c_1_sum_ from (select avg(score) c_2, sum(score) c_1, course_id c_0 from student_course group by course_id) t_", searchSql.getClusterSqlString());
                Assert.assertTrue(searchSql.getClusterSqlParams().isEmpty());
                return new SqlResult<>(searchSql, null, columnLabel -> 100);
            }
        };

        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        Assert.assertEquals(100, mapSearcher.searchSum(StudentCourse.class, new HashMap<>(), "sumScore"));
        Assert.assertArrayEquals(new Number[]{100}, mapSearcher.searchSum(StudentCourse.class, new HashMap<>(), new String[]{"sumScore"}));
        Assert.assertEquals(100, mapSearcher.searchSum(StudentCourse.class, null, "sumScore"));
        Assert.assertArrayEquals(new Number[]{100}, mapSearcher.searchSum(StudentCourse.class, null, new String[]{"sumScore"}));

        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();
        Assert.assertEquals(100, beanSearcher.searchSum(StudentCourse.class, new HashMap<>(), "sumScore"));
        Assert.assertArrayEquals(new Number[]{100}, beanSearcher.searchSum(StudentCourse.class, new HashMap<>(), new String[]{"sumScore"}));
        Assert.assertEquals(100, beanSearcher.searchSum(StudentCourse.class, null, "sumScore"));
        Assert.assertArrayEquals(new Number[]{100}, beanSearcher.searchSum(StudentCourse.class, null, new String[]{"sumScore"}));
    }

}
