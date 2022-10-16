package cn.zhxu.bs;

import cn.zhxu.bs.bean.DbField;
import cn.zhxu.bs.bean.SearchBean;
import cn.zhxu.bs.operator.GreaterThan;
import cn.zhxu.bs.util.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

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

    @Test
    public void test_where() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                Assert.assertNull(searchSql.getClusterSqlString());
                Assert.assertTrue(searchSql.getClusterSqlParams().isEmpty());
                Assert.assertEquals("select avg(score) c_2, sum(score) c_1, course_id c_0 from student_course where (course_id = ?) group by course_id limit ?, ?", searchSql.getListSqlString());
                List<Object> params = searchSql.getListSqlParams();
                Assert.assertEquals(3, params.size());
                Assert.assertEquals(5L, params.get(0));
                Assert.assertEquals(0L, params.get(1));
                Assert.assertEquals(1, params.get(2));
                return new SqlResult<>(searchSql, new SqlResult.ResultSet() {
                    @Override
                    public boolean next() {
                        return false;
                    }
                    @Override
                    public Object get(String columnLabel) {
                        return 0;
                    }
                }, columnLabel -> 100);
            }
        };
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        mapSearcher.searchFirst(StudentCourse.class, MapUtils.builder().field(StudentCourse::getCourseId, 5).build());
        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();
        beanSearcher.searchFirst(StudentCourse.class, MapUtils.builder().field(StudentCourse::getCourseId, 5).build());
    }


    @Test
    public void test_having() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                Assert.assertNull(searchSql.getClusterSqlString());
                Assert.assertTrue(searchSql.getClusterSqlParams().isEmpty());
                Assert.assertEquals("select avg(score) c_2, sum(score) c_1, course_id c_0 from student_course group by course_id having (c_2 > ?) limit ?, ?", searchSql.getListSqlString());
                List<Object> params = searchSql.getListSqlParams();
                Assert.assertEquals(3, params.size());
                Assert.assertEquals(80, params.get(0));
                Assert.assertEquals(0L, params.get(1));
                Assert.assertEquals(1, params.get(2));
                return new SqlResult<>(searchSql, new SqlResult.ResultSet() {
                    @Override
                    public boolean next() {
                        return false;
                    }
                    @Override
                    public Object get(String columnLabel) {
                        return 0;
                    }
                }, columnLabel -> 100);
            }
        };
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        mapSearcher.searchFirst(StudentCourse.class, MapUtils.builder().field(StudentCourse::getAvgScore, 80).op(GreaterThan.class).build());
        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();
        beanSearcher.searchFirst(StudentCourse.class, MapUtils.builder().field(StudentCourse::getAvgScore, 80).op(GreaterThan.class).build());
    }


    @Test
    public void test_where_having() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                Assert.assertNull(searchSql.getClusterSqlString());
                Assert.assertTrue(searchSql.getClusterSqlParams().isEmpty());
                Assert.assertEquals("select avg(score) c_2, sum(score) c_1, course_id c_0 from student_course where (course_id = ?) group by course_id having (c_2 > ?) limit ?, ?", searchSql.getListSqlString());
                List<Object> params = searchSql.getListSqlParams();
                Assert.assertEquals(4, params.size());
                Assert.assertEquals(5L, params.get(0));
                Assert.assertEquals(80, params.get(1));
                Assert.assertEquals(0L, params.get(2));
                Assert.assertEquals(1, params.get(3));
                return new SqlResult<>(searchSql, new SqlResult.ResultSet() {
                    @Override
                    public boolean next() {
                        return false;
                    }
                    @Override
                    public Object get(String columnLabel) {
                        return 0;
                    }
                }, columnLabel -> 100);
            }
        };
        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        mapSearcher.searchFirst(StudentCourse.class, MapUtils.builder().field(StudentCourse::getCourseId, 5)
                .field(StudentCourse::getAvgScore, 80).op(GreaterThan.class).build());
        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();
        beanSearcher.searchFirst(StudentCourse.class, MapUtils.builder().field(StudentCourse::getCourseId, 5)
                .field(StudentCourse::getAvgScore, 80).op(GreaterThan.class).build());
    }

    @SearchBean(groupBy = "course_id", having = "sum_score > 100")
    public static class StudentCourse2 {
        private long courseId;
        @DbField(value = "sum(score)", alias = "sum_score")
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
    public void test_count_having_1() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                System.out.println(searchSql.getClusterSqlString());
                Assert.assertNull(searchSql.getListSqlString());
                Assert.assertTrue(searchSql.getListSqlParams().isEmpty());
                Assert.assertEquals("select count(*) s_count from (select avg(score) c_2, sum(score) sum_score, course_id c_1 from student_course2 group by course_id having (sum_score > 100)) t_", searchSql.getClusterSqlString());
                Assert.assertTrue(searchSql.getClusterSqlParams().isEmpty());
                return new SqlResult<>(searchSql, null, columnLabel -> 100);
            }
        };

        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        Assert.assertEquals(100, mapSearcher.searchCount(StudentCourse2.class, new HashMap<>()));
        Assert.assertEquals(100, mapSearcher.searchCount(StudentCourse2.class, null));

        BeanSearcher beanSearcher = SearcherBuilder.beanSearcher().sqlExecutor(sqlExecutor).build();
        Assert.assertEquals(100, beanSearcher.searchCount(StudentCourse2.class, new HashMap<>()));
        Assert.assertEquals(100, beanSearcher.searchCount(StudentCourse2.class, null));
    }

    @Test
    public void test_count_having_2() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                System.out.println(searchSql.getClusterSqlString());
                Assert.assertNull(searchSql.getListSqlString());
                Assert.assertTrue(searchSql.getListSqlParams().isEmpty());
                Assert.assertEquals("select count(*) s_count from (select avg(score) c_2, sum(score) sum_score, course_id c_1 from student_course2 where (course_id = ?) group by course_id having (sum_score > 100)) t_", searchSql.getClusterSqlString());
                List<Object> params = searchSql.getClusterSqlParams();
                Assert.assertEquals(1, params.size());
                Assert.assertEquals(5L, params.get(0));
                return new SqlResult<>(searchSql, null, columnLabel -> 100);
            }
        };

        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        Assert.assertEquals(100, mapSearcher.searchCount(StudentCourse2.class, MapUtils.builder().field(StudentCourse::getCourseId, 5).build()));
    }


    @Test
    public void test_count_having_3() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                System.out.println(searchSql.getClusterSqlString());
                Assert.assertNull(searchSql.getListSqlString());
                Assert.assertTrue(searchSql.getListSqlParams().isEmpty());
                Assert.assertEquals("select count(*) s_count from (select avg(score) c_2, sum(score) sum_score, course_id c_1 from student_course2 group by course_id having (sum_score > 100) and (c_2 > ?)) t_", searchSql.getClusterSqlString());
                List<Object> params = searchSql.getClusterSqlParams();
                Assert.assertEquals(1, params.size());
                Assert.assertEquals(80, params.get(0));
                return new SqlResult<>(searchSql, null, columnLabel -> 100);
            }
        };

        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        Assert.assertEquals(100, mapSearcher.searchCount(StudentCourse2.class, MapUtils.builder().field(StudentCourse::getAvgScore, 80).op(GreaterThan.class).build()));
    }

    @Test
    public void test_count_having_4() {
        SqlExecutor sqlExecutor = new SqlExecutor() {
            @Override
            public <T> SqlResult<T> execute(SearchSql<T> searchSql) {
                System.out.println(searchSql.getClusterSqlString());
                Assert.assertNull(searchSql.getListSqlString());
                Assert.assertTrue(searchSql.getListSqlParams().isEmpty());
                Assert.assertEquals("select count(*) s_count from (select avg(score) c_2, sum(score) sum_score, course_id c_1 from student_course2 where (course_id = ?) group by course_id having (sum_score > 100) and (c_2 > ?)) t_", searchSql.getClusterSqlString());
                List<Object> params = searchSql.getClusterSqlParams();
                Assert.assertEquals(2, params.size());
                Assert.assertEquals(5L, params.get(0));
                Assert.assertEquals(80, params.get(1));
                return new SqlResult<>(searchSql, null, columnLabel -> 100);
            }
        };

        MapSearcher mapSearcher = SearcherBuilder.mapSearcher().sqlExecutor(sqlExecutor).build();
        Assert.assertEquals(100, mapSearcher.searchCount(StudentCourse2.class, MapUtils.builder().field(StudentCourse::getCourseId, 5)
                .field(StudentCourse::getAvgScore, 80).op(GreaterThan.class).build()));
    }

}
