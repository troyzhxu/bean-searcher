package cn.zhxu.bs.operator;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.dialect.MySqlDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BetweenTestCase {

    final Between between = new Between();

    public BetweenTestCase() {
        between.setDialect(new MySqlDialect());
    }

    @Test
    public void test_01() {
        Assertions.assertEquals("Between", between.name());
        Assertions.assertTrue(between.isNamed("bt"));
        Assertions.assertTrue(between.isNamed("Between"));
        Assertions.assertFalse(between.lonely());
        System.out.println("\ttest_01 ok!");
    }

    @Test
    public void test_02() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("age"), false, new Object[]{ 10, 20 }
        ));
        Assertions.assertEquals("age between ? and ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 10, 20 }, params.toArray());
        System.out.println("\ttest_02 passed");
    }

    @Test
    public void test_03() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("age"), false, new Object[]{ 10 }
        ));
        Assertions.assertEquals("age >= ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 10 }, params.toArray());
        System.out.println("\ttest_03 passed");
    }

    @Test
    public void test_04() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("age"), false, new Object[]{ 10, null }
        ));
        Assertions.assertEquals("age >= ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 10 }, params.toArray());
        System.out.println("\ttest_04 passed");
    }

    @Test
    public void test_05() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("age"), false, new Object[]{ null, 10 }
        ));
        Assertions.assertEquals("age <= ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 10 }, params.toArray());
        System.out.println("\ttest_05 passed");
    }

    @Test
    public void test_06() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("age"), false, new Object[]{ 10, " " }
        ));
        Assertions.assertEquals("age >= ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 10 }, params.toArray());
        System.out.println("\ttest_06 passed");
    }

    @Test
    public void test_07() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("age"), false, new Object[]{ " ", 10 }
        ));
        Assertions.assertEquals("age <= ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 10 }, params.toArray());
        System.out.println("\ttest_07 passed");
    }

    @Test
    public void test_08() {
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select age from user where id = ?)");
        fieldSql.addPara(120);
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, false, new Object[]{ 10, 20 }
        ));
        Assertions.assertEquals("(select age from user where id = ?) between ? and ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 120, 10, 20 }, params.toArray());
        System.out.println("\ttest_08 passed");
    }

    @Test
    public void test_09() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), true, new Object[]{ 'A', 'b' }
        ));
        Assertions.assertEquals("upper(name) between ? and ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ "A", "B" }, params.toArray());
        System.out.println("\ttest_09 passed");
    }

    @Test
    public void test_10() {
        StringBuilder sb = new StringBuilder();
        List<Object> params = between.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), true, new Object[]{ "Abc", "Abc" }
        ));
        Assertions.assertEquals("upper(name) between ? and ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ "ABC", "ABC" }, params.toArray());
        System.out.println("\ttest_10 passed");
    }

}
