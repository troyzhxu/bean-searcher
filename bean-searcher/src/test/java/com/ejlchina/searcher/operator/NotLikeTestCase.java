package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.SqlWrapper;
import com.ejlchina.searcher.dialect.MySqlDialect;
import com.ejlchina.searcher.dialect.PostgreSqlDialect;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class NotLikeTestCase {

    final NotLike notLike = new NotLike();

    @Test
    public void test_01() {
        Assert.assertEquals("NotLike", notLike.name());
        Assert.assertTrue(notLike.isNamed("nk"));
        Assert.assertTrue(notLike.isNamed("NotLike"));
        Assert.assertFalse(notLike.lonely());
        System.out.println("NotLikeTestCase test_01 passed");
    }

    @Test
    public void test_02() {
        notLike.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                new SqlWrapper<>("name"), false, new Object[]{ "abc%" }
        ));
        Assert.assertEquals("name not like ?", sb.toString());
        Assert.assertArrayEquals(new Object[] { "abc%" }, params.toArray());
        System.out.println("NotLikeTestCase test_02 passed");
    }

    @Test
    public void test_03() {
        notLike.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                new SqlWrapper<>("name"), true, new Object[]{ "abc%" }
        ));
        Assert.assertEquals("upper(name) not like ?", sb.toString());
        Assert.assertArrayEquals(new Object[] { "ABC%" }, params.toArray());
        System.out.println("NotLikeTestCase test_03 passed");
    }

    @Test
    public void test_04() {
        notLike.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                fieldSql, false, new Object[]{ "abc%" }
        ));
        Assert.assertEquals("(select name from user where id = ?) not like ?", sb.toString());
        Assert.assertArrayEquals(new Object[] { 12, "abc%" }, params.toArray());
        System.out.println("NotLikeTestCase test_04 passed");
    }

    @Test
    public void test_05() {
        notLike.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                fieldSql, true, new Object[]{ "abc%" }
        ));
        Assert.assertEquals("upper((select name from user where id = ?)) not like ?", sb.toString());
        Assert.assertArrayEquals(new Object[] { 12, "ABC%" }, params.toArray());
        System.out.println("NotLikeTestCase test_05 passed");
    }

    @Test
    public void test_06() {
        notLike.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                new SqlWrapper<>("name"), false, new Object[]{ "abc%" }
        ));
        Assert.assertEquals("name not like ?", sb.toString());
        Assert.assertArrayEquals(new Object[] { "abc%" }, params.toArray());
        System.out.println("NotLikeTestCase test_06 passed");
    }

    @Test
    public void test_07() {
        notLike.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                new SqlWrapper<>("name"), true, new Object[]{ "abc%" }
        ));
        Assert.assertEquals("name not ilike ?", sb.toString());
        Assert.assertArrayEquals(new Object[] { "abc%" }, params.toArray());
        System.out.println("NotLikeTestCase test_07 passed");
    }

    @Test
    public void test_08() {
        notLike.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                fieldSql, false, new Object[] { "abc%" }
        ));
        Assert.assertEquals("(select name from user where id = ?) not like ?", sb.toString());
        Assert.assertArrayEquals(new Object[]{ 12, "abc%" }, params.toArray());
        System.out.println("NotLikeTestCase test_08 passed");
    }

    @Test
    public void test_09() {
        notLike.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = notLike.operate(sb, new FieldOp.OpPara(
                fieldSql, true, new Object[] { "abc%" }
        ));
        Assert.assertEquals("(select name from user where id = ?) not ilike ?", sb.toString());
        Assert.assertArrayEquals(new Object[]{ 12, "abc%" }, params.toArray());
        System.out.println("NotLikeTestCase test_09 passed");
    }

}
