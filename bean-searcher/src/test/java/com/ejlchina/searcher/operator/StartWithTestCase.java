package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.SqlWrapper;
import com.ejlchina.searcher.dialect.MySqlDialect;
import com.ejlchina.searcher.dialect.PostgreSqlDialect;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class StartWithTestCase {

    final StartWith startWith = new StartWith();

    @Test
    public void test_01() {
        Assert.assertEquals("StartWith", startWith.name());
        Assert.assertTrue(startWith.isNamed("sw"));
        Assert.assertTrue(startWith.isNamed("StartWith"));
        Assert.assertFalse(startWith.lonely());
        System.out.println("StartWithTestCase test_01 passed");
    }

    @Test
    public void test_02() {
        startWith.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = startWith.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), false, new Object[]{ "abc" }
        ));
        Assert.assertEquals("name like ?", sb.toString());
        Assert.assertArrayEquals(new Object[] { "abc%" }, params.toArray());
        System.out.println("StartWithTestCase test_02 passed");
    }

    @Test
    public void test_03() {
        startWith.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = startWith.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), true, new Object[]{ "abc" }
        ));
        Assert.assertEquals("upper(name) like ?", sb.toString());
        Assert.assertArrayEquals(new Object[] { "ABC%" }, params.toArray());
        System.out.println("StartWithTestCase test_03 passed");
    }

    @Test
    public void test_04() {
        startWith.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = startWith.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, false, new Object[]{ "abc" }
        ));
        Assert.assertEquals("(select name from user where id = ?) like ?", sb.toString());
        Assert.assertArrayEquals(new Object[] { 12, "abc%" }, params.toArray());
        System.out.println("StartWithTestCase test_04 passed");
    }

    @Test
    public void test_05() {
        startWith.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = startWith.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, true, new Object[]{ "abc" }
        ));
        Assert.assertEquals("upper((select name from user where id = ?)) like ?", sb.toString());
        Assert.assertArrayEquals(new Object[] { 12, "ABC%" }, params.toArray());
        System.out.println("StartWithTestCase test_05 passed");
    }

    @Test
    public void test_06() {
        startWith.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = startWith.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), false, new Object[]{ "abc" }
        ));
        Assert.assertEquals("name like ?", sb.toString());
        Assert.assertArrayEquals(new Object[] { "abc%" }, params.toArray());
        System.out.println("StartWithTestCase test_06 passed");
    }

    @Test
    public void test_07() {
        startWith.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = startWith.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), true, new Object[]{ "abc" }
        ));
        Assert.assertEquals("name ilike ?", sb.toString());
        Assert.assertArrayEquals(new Object[] { "abc%" }, params.toArray());
        System.out.println("StartWithTestCase test_07 passed");
    }

    @Test
    public void test_08() {
        startWith.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = startWith.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, false, new Object[] { "abc" }
        ));
        Assert.assertEquals("(select name from user where id = ?) like ?", sb.toString());
        Assert.assertArrayEquals(new Object[]{ 12, "abc%" }, params.toArray());
        System.out.println("StartWithTestCase test_08 passed");
    }

    @Test
    public void test_09() {
        startWith.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = startWith.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, true, new Object[] { "abc" }
        ));
        Assert.assertEquals("(select name from user where id = ?) ilike ?", sb.toString());
        Assert.assertArrayEquals(new Object[]{ 12, "abc%" }, params.toArray());
        System.out.println("StartWithTestCase test_09 passed");
    }

}
