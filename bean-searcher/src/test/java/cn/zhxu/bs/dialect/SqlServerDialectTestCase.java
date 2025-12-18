package cn.zhxu.bs.dialect;

import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.param.Paging;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SqlServerDialectTestCase {

    Dialect dialect = new SqlServerDialect();

    @Test
    public void test_01() {
        SqlWrapper<Object> wrapper = dialect.forPaginate("select id, name", " from user where id > 1", null);
        Assertions.assertEquals("select id, name from user where id > 1", wrapper.getSql());
        Assertions.assertTrue(wrapper.getParas().isEmpty());
        System.out.println("\tSqlServerDialect test_01 ok!");
    }

    @Test
    public void test_02() {
        SqlWrapper<Object> wrapper = dialect.forPaginate("select id, name", " from user where id > 1", new Paging(1, 0));
        Assertions.assertEquals("select top 1 id, name from user where id > 1", wrapper.getSql());
        Assertions.assertTrue(wrapper.getParas().isEmpty());
        System.out.println("\tSqlServerDialect test_02 ok!");
    }

    @Test
    public void test_03() {
        SqlWrapper<Object> wrapper = dialect.forPaginate("select id, name", " from user where id > 1", new Paging(15, 0));
        Assertions.assertEquals("select top 15 id, name from user where id > 1", wrapper.getSql());
        Assertions.assertTrue(wrapper.getParas().isEmpty());
        System.out.println("\tSqlServerDialect test_03 ok!");
    }

    @Test
    public void test_04() {
        SqlWrapper<Object> wrapper = dialect.forPaginate("select id, name", " from user where id > 1", new Paging(15, 30));
        Assertions.assertEquals("select id, name from user where id > 1 order by null offset ? rows fetch next ? rows only", wrapper.getSql());
        List<Object> paras = wrapper.getParas();
        Assertions.assertEquals(2, paras.size());
        Assertions.assertEquals(30L, paras.get(0));
        Assertions.assertEquals(15, paras.get(1));
        System.out.println("\tSqlServerDialect test_04 ok!");
    }

    @Test
    public void test_05() {
        Paging paging = new Paging(15, 30);
        paging.setOrdering(true);
        SqlWrapper<Object> wrapper = dialect.forPaginate("select id, name", " from user where id > 1 order by id", paging);
        Assertions.assertEquals("select id, name from user where id > 1 order by id offset ? rows fetch next ? rows only", wrapper.getSql());
        List<Object> paras = wrapper.getParas();
        Assertions.assertEquals(2, paras.size());
        Assertions.assertEquals(30L, paras.get(0));
        Assertions.assertEquals(15, paras.get(1));
        System.out.println("\tSqlServerDialect test_05 ok!");
    }

}
