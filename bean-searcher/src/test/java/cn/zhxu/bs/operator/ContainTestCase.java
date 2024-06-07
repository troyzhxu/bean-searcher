package cn.zhxu.bs.operator;

import cn.zhxu.bs.FieldOp;
import cn.zhxu.bs.SqlWrapper;
import cn.zhxu.bs.dialect.MySqlDialect;
import cn.zhxu.bs.dialect.PostgreSqlDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ContainTestCase {

    final Contain contain = new Contain();

    @Test
    public void test_01() {
        Assertions.assertEquals("Contain", contain.name());
        Assertions.assertTrue(contain.isNamed("ct"));
        Assertions.assertTrue(contain.isNamed("Contain"));
        Assertions.assertFalse(contain.lonely());
        System.out.println("\ttest_01 passed");
    }

    @Test
    public void test_02() {
        contain.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = contain.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), false, new Object[]{ "abc" }
        ));
        Assertions.assertEquals("name like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { "%abc%" }, params.toArray());
        System.out.println("\ttest_02 passed");
    }

    @Test
    public void test_03() {
        contain.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = contain.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), true, new Object[]{ "abc" }
        ));
        Assertions.assertEquals("upper(name) like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { "%ABC%" }, params.toArray());
        System.out.println("\ttest_03 passed");
    }

    @Test
    public void test_04() {
        contain.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = contain.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, false, new Object[]{ "abc" }
        ));
        Assertions.assertEquals("(select name from user where id = ?) like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { 12, "%abc%" }, params.toArray());
        System.out.println("\ttest_04 passed");
    }

    @Test
    public void test_05() {
        contain.setDialect(new MySqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = contain.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, true, new Object[]{ "abc" }
        ));
        Assertions.assertEquals("upper((select name from user where id = ?)) like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { 12, "%ABC%" }, params.toArray());
        System.out.println("\ttest_05 passed");
    }

    @Test
    public void test_06() {
        contain.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = contain.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), false, new Object[]{ "abc" }
        ));
        Assertions.assertEquals("name like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { "%abc%" }, params.toArray());
        System.out.println("\ttest_06 passed");
    }

    @Test
    public void test_07() {
        contain.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        List<Object> params = contain.operate(sb, new FieldOp.OpPara(
                name -> new SqlWrapper<>("name"), true, new Object[]{ "abc" }
        ));
        Assertions.assertEquals("name ilike ?", sb.toString());
        Assertions.assertArrayEquals(new Object[] { "%abc%" }, params.toArray());
        System.out.println("\ttest_07 passed");
    }

    @Test
    public void test_08() {
        contain.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = contain.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, false, new Object[] { "abc" }
        ));
        Assertions.assertEquals("(select name from user where id = ?) like ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 12, "%abc%" }, params.toArray());
        System.out.println("\ttest_08 passed");
    }

    @Test
    public void test_09() {
        contain.setDialect(new PostgreSqlDialect());
        StringBuilder sb = new StringBuilder();
        SqlWrapper<Object> fieldSql = new SqlWrapper<>("(select name from user where id = ?)");
        fieldSql.addPara(12);
        List<Object> params = contain.operate(sb, new FieldOp.OpPara(
                name -> fieldSql, true, new Object[] { "abc" }
        ));
        Assertions.assertEquals("(select name from user where id = ?) ilike ?", sb.toString());
        Assertions.assertArrayEquals(new Object[]{ 12, "%abc%" }, params.toArray());
        System.out.println("\ttest_09 passed");
    }

}
