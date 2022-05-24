package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;
import com.ejlchina.searcher.SqlWrapper;
import com.ejlchina.searcher.dialect.MySqlDialect;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class BetweenTestCase {

    final Between between = new Between();

    public BetweenTestCase() {
        between.setDialect(new MySqlDialect());
    }

    @Test
    public void test_01() {
        Assert.assertEquals("Between", between.name());
        Assert.assertTrue(between.isNamed("bt"));
        Assert.assertTrue(between.isNamed("Between"));
        Assert.assertFalse(between.lonely());
        System.out.println("BetweenTestCase test_01 passed");
    }

    @Test
    public void test_02() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                new SqlWrapper<>("age"), false, new Object[]{ 10, 20 }
        ));
        Assert.assertEquals("age between ? and ?", sb.toString());
        Assert.assertArrayEquals(new Object[]{ 10, 20 }, params.toArray());
        System.out.println("BetweenTestCase test_02 passed");
    }

    @Test
    public void test_03() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                new SqlWrapper<>("age"), false, new Object[]{ 10 }
        ));
        Assert.assertEquals("age >= ?", sb.toString());
        Assert.assertArrayEquals(new Object[]{ 10 }, params.toArray());
        System.out.println("BetweenTestCase test_03 passed");
    }

    @Test
    public void test_04() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                new SqlWrapper<>("age"), false, new Object[]{ 10, null }
        ));
        Assert.assertEquals("age >= ?", sb.toString());
        Assert.assertArrayEquals(new Object[]{ 10 }, params.toArray());
        System.out.println("BetweenTestCase test_04 passed");
    }

    @Test
    public void test_05() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                new SqlWrapper<>("age"), false, new Object[]{ null, 10 }
        ));
        Assert.assertEquals("age <= ?", sb.toString());
        Assert.assertArrayEquals(new Object[]{ 10 }, params.toArray());
        System.out.println("BetweenTestCase test_05 passed");
    }

    @Test
    public void test_06() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                new SqlWrapper<>("age"), false, new Object[]{ 10, " " }
        ));
        Assert.assertEquals("age >= ?", sb.toString());
        Assert.assertArrayEquals(new Object[]{ 10 }, params.toArray());
        System.out.println("BetweenTestCase test_06 passed");
    }

    @Test
    public void test_07() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                new SqlWrapper<>("age"), false, new Object[]{ " ", 10 }
        ));
        Assert.assertEquals("age <= ?", sb.toString());
        Assert.assertArrayEquals(new Object[]{ 10 }, params.toArray());
        System.out.println("BetweenTestCase test_07 passed");
    }

    @Test
    public void test_08() {
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(120);
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                fieldSql, false, new Object[]{ 10, 20 }
        ));
        Assert.assertEquals("(select name from user where id = ?) between ? and ?", sb.toString());
        Assert.assertArrayEquals(new Object[]{ 120, 10, 20 }, params.toArray());
        System.out.println("BetweenTestCase test_08 passed");
    }

    @Test
    public void test_09() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                new SqlWrapper<>("name"), true, new Object[]{ 'A', 'b' }
        ));
        Assert.assertEquals("upper(name) between ? and ?", sb.toString());
        Assert.assertArrayEquals(new Object[]{ "A", "B" }, params.toArray());
        System.out.println("BetweenTestCase test_09 passed");
    }

    @Test
    public void test_10() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                new SqlWrapper<>("name"), true, new Object[]{ "Abc", "Abc" }
        ));
        Assert.assertEquals("upper(name) between ? and ?", sb.toString());
        Assert.assertArrayEquals(new Object[]{ "ABC", "ABC" }, params.toArray());
        System.out.println("BetweenTestCase test_10 passed");
    }

}
